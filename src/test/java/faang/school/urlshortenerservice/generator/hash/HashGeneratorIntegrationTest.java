package faang.school.urlshortenerservice.generator.hash;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableAsync
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class HashGeneratorIntegrationTest extends BaseContextTest {

    @Nested
    class PositiveScenarios {

        @Autowired
        private HashGenerator hashGenerator;
        @Autowired
        private HashRepository hashRepository;

        @Test
        @DisplayName("When 50 almost simultaneously generateBatch called then runs async")
        public void whenMethodCalledMultipleTimesThenGenerateHashBatchesAsync() throws InterruptedException {
            Thread.sleep(1000);
            for (int i = 0; i < 50; i++) {
                hashGenerator.generateBatch();
            }
            Thread.sleep(2000);
            List<Hash> savedHashes = hashRepository.findAll();
            assertFalse(savedHashes.isEmpty());
            assertEquals(10000, savedHashes.size());
        }
    }

    @Nested
    class ExceptionScenarios {

        @Autowired
        private HashGenerator hashGenerator;
        @MockBean
        private HashRepository hashRepository;

        /**
         * This mock is needed for isolation from @PostConstruct method
         */
        @MockBean
        private HashCache hashCache;

        @Test
        @DisplayName("Testing exception catches while problems on batch generation")
        public void whenSomethingGoesWrongWhileGeneratingBatchThenThrowDaeException() {
            when(hashRepository.getUniqueNumbers(anyInt()))
                    .thenThrow(new DataAccessException("Test exception!") {
                    });
            try {
                hashGenerator.generateBatch();
            } catch (RuntimeException e) {
                assertTrue(e.getMessage().contains("Test exception!"));
            }
            verify(hashRepository).getUniqueNumbers(anyInt());
        }

        @Nested
        class BatchForCacheIsolationTest {

            @Test
            @DisplayName("Testing exception catches while problems on batch generation for cache")
            public void whenSomethingGoesWrongWhileGeneratingBatchForCacheThenThrowDaeException() {
                when(hashRepository.getUniqueNumbers(anyInt()))
                        .thenThrow(new DataAccessException("Test exception!") {
                        });
                try {
                    CompletableFuture<List<Hash>> future = hashGenerator.generateBatchForCache(10);
                    assertThrows(RuntimeException.class, future::join);
                } catch (RuntimeException e) {
                    assertTrue(e.getMessage().contains("Test exception!"));
                }
                verify(hashRepository).getUniqueNumbers(anyInt());
            }
        }
    }
}
