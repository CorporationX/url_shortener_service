package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CacheRefillServiceTest {

    @BeforeEach
    void setUp() {
        hashGenerator = mock(HashGenerator.class);
        cacheRefillService = new CacheRefillService(hashGenerator);
    }

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private CacheRefillService cacheRefillService;

    @Nested
    @DisplayName("refillCache tests")
    class RefillCacheTests {

        @Test
        @DisplayName("Should refill cache with available hashes")
        void shouldRefillCache() {
            int spaceAvailable = 5;
            List<String> expectedHashes = Arrays.asList("abc123", "def456", "ghi789");
            when(hashGenerator.getAvailableHashes(spaceAvailable)).thenReturn(expectedHashes);
            List<String> result = cacheRefillService.refillCache(spaceAvailable);
            assertThat(result)
                    .hasSize(3)
                    .containsExactlyElementsOf(expectedHashes);
            verify(hashGenerator).getAvailableHashes(spaceAvailable);
        }

        @Test
        @DisplayName("Should return empty list when no space available")
        void shouldReturnEmptyListWhenNoSpace() {
            int spaceAvailable = 0;
            List<String> result = cacheRefillService.refillCache(spaceAvailable);
            assertThat(result).isEmpty();
            verify(hashGenerator, never()).getAvailableHashes(anyInt());
        }

        @Test
        @DisplayName("Should return empty list for negative space")
        void shouldReturnEmptyListForNegativeSpace() {
            int spaceAvailable = -5;
            List<String> result = cacheRefillService.refillCache(spaceAvailable);
            assertThat(result).isEmpty();
            verify(hashGenerator, never()).getAvailableHashes(anyInt());
        }

        @Test
        @DisplayName("Should handle empty result from generator")
        void shouldHandleEmptyResultFromGenerator() {
            int spaceAvailable = 10;
            when(hashGenerator.getAvailableHashes(spaceAvailable)).thenReturn(Collections.emptyList());
            List<String> result = cacheRefillService.refillCache(spaceAvailable);
            assertThat(result).isEmpty();
            verify(hashGenerator).getAvailableHashes(spaceAvailable);
        }
    }

    @Nested
    @DisplayName("generateNewHashes tests")
    class GenerateNewHashesTests {

        @Test
        @DisplayName("Should generate new hashes successfully")
        void shouldGenerateNewHashes() {
            CompletableFuture<Void> expectedFuture = CompletableFuture.completedFuture(null);
            when(hashGenerator.generateHashes()).thenReturn(expectedFuture);
            CompletableFuture<Void> result = cacheRefillService.generateNewHashes();
            assertThat(result).isEqualTo(expectedFuture);
            verify(hashGenerator).generateHashes();
        }

        @Test
        @DisplayName("Should handle generation failure")
        void shouldHandleGenerationFailure() {
            RuntimeException expectedException = new RuntimeException("Generation failed");
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(expectedException);
            when(hashGenerator.generateHashes()).thenReturn(failedFuture);
            CompletableFuture<Void> result = cacheRefillService.generateNewHashes();
            assertThatThrownBy(result::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCause(expectedException);
            verify(hashGenerator).generateHashes();
        }
    }
}