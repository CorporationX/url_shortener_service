package faang.school.urlshortenerservice.generator.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
