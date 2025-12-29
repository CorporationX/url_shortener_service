package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.properties.HashGeneratorProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HashGeneratorTest {

    @Mock
    private HashGeneratorProperties properties;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder encoder;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Timer timer;

    @Mock
    private Counter successCounter;

    @Mock
    private Counter errorCounter;

    @InjectMocks
    private HashGenerator hashGenerator;

    private HashGeneratorProperties.Retry retryConfig;
    private HashGeneratorProperties.ThreadPool threadPoolConfig;

    @BeforeEach
    void setUp() {
        retryConfig = new HashGeneratorProperties.Retry();
        retryConfig.setMaxAttempts(3);
        retryConfig.setDelayMs(100L);

        threadPoolConfig = new HashGeneratorProperties.ThreadPool();
        threadPoolConfig.setSize(4);
        threadPoolConfig.setQueueCapacity(10000);

        when(properties.getRetry()).thenReturn(retryConfig);
        when(properties.getBatchSize()).thenReturn(5);
        when(properties.getThreadPool()).thenReturn(threadPoolConfig);

        when(meterRegistry.timer(anyString())).thenReturn(timer);
        when(meterRegistry.counter(eq("hash.generation.success"), anyString(), anyString())).thenReturn(successCounter);
        when(meterRegistry.counter(eq("hash.generation.error"), anyString(), anyString())).thenReturn(errorCounter);
    }

    @Test
    void testGenerateHashBatch_Success() throws Exception {
        // Arrange
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> hashes = List.of("1", "2", "3", "4", "5");

        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(hashes);
        doNothing().when(hashRepository).save(hashes);

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        future.get(5, TimeUnit.SECONDS);

        // Assert
        verify(hashRepository).getUniqueNumbers(5);
        verify(encoder).encode(uniqueNumbers);
        verify(hashRepository).save(hashes);
        verify(meterRegistry).timer("hash.generation.duration");
        verify(meterRegistry).counter("hash.generation.success", "batch_size", "5");
        verify(successCounter).increment();
    }

    @Test
    void testGenerateHashBatch_EmptyUniqueNumbers() throws Exception {
        // Arrange
        when(hashRepository.getUniqueNumbers(5)).thenReturn(List.of());

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        future.get(5, TimeUnit.SECONDS);

        // Assert
        verify(hashRepository).getUniqueNumbers(5);
        verify(encoder, never()).encode(any());
        verify(hashRepository, never()).save(any());
        verify(meterRegistry).timer("hash.generation.duration");
        verify(meterRegistry, never()).counter(eq("hash.generation.success"), anyString(), anyString());
    }

    @Test
    void testGenerateHashBatch_NullUniqueNumbers() throws Exception {
        // Arrange
        when(hashRepository.getUniqueNumbers(5)).thenReturn(null);

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        future.get(5, TimeUnit.SECONDS);

        // Assert
        verify(hashRepository).getUniqueNumbers(5);
        verify(encoder, never()).encode(any());
        verify(hashRepository, never()).save(any());
    }

    @Test
    void testGenerateHashBatch_EmptyHashes() throws Exception {
        // Arrange
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(List.of());

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        
        // Assert - должно выбросить исключение после retry
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Ожидаем исключение после всех попыток
        }

        verify(hashRepository, atLeast(1)).getUniqueNumbers(5);
        verify(encoder, atLeast(1)).encode(uniqueNumbers);
        verify(hashRepository, never()).save(any());
        verify(meterRegistry, never()).timer("hash.generation.duration");
        verify(meterRegistry).counter("hash.generation.error", "exception", "CompletionException");
        verify(errorCounter).increment();
    }

    @Test
    void testGenerateHashBatch_HashCountMismatch() throws Exception {
        // Arrange
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("1", "2"); // Меньше чем чисел
        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(hashes);

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        
        // Assert - должно выбросить исключение после retry
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Ожидаем исключение после всех попыток
        }

        verify(hashRepository, atLeast(1)).getUniqueNumbers(5);
        verify(encoder, atLeast(1)).encode(uniqueNumbers);
        verify(hashRepository, never()).save(any());
        verify(meterRegistry, never()).timer("hash.generation.duration");
        verify(meterRegistry).counter("hash.generation.error", "exception", "CompletionException");
        verify(errorCounter).increment();
    }

    @Test
    void testGenerateHashBatch_RepositoryThrowsException() throws Exception {
        // Arrange
        when(hashRepository.getUniqueNumbers(5)).thenThrow(new RuntimeException("Database error"));

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        
        // Assert - должно выбросить исключение после retry
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Ожидаем исключение после всех попыток
        }

        verify(hashRepository, atLeast(1)).getUniqueNumbers(5);
        verify(meterRegistry, never()).timer("hash.generation.duration");
        verify(meterRegistry).counter("hash.generation.error", "exception", "CompletionException");
        verify(errorCounter).increment();
    }

    @Test
    void testGenerateHashBatch_EncoderThrowsException() throws Exception {
        // Arrange
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenThrow(new RuntimeException("Encoding error"));

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        
        // Assert - должно выбросить исключение после retry
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Ожидаем исключение после всех попыток
        }

        verify(hashRepository, atLeast(1)).getUniqueNumbers(5);
        verify(encoder, atLeast(1)).encode(uniqueNumbers);
        verify(hashRepository, never()).save(any());
        verify(meterRegistry, never()).timer("hash.generation.duration");
        verify(meterRegistry).counter("hash.generation.error", "exception", "CompletionException");
        verify(errorCounter).increment();
    }

    @Test
    void testGenerateHashBatch_SaveThrowsException() throws Exception {
        // Arrange
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> hashes = List.of("1", "2", "3");
        when(hashRepository.getUniqueNumbers(5)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(hashes);
        doThrow(new RuntimeException("Save error")).when(hashRepository).save(hashes);

        // Act
        CompletableFuture<Void> future = hashGenerator.generateHashBatch();
        
        // Assert - должно выбросить исключение после retry
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Ожидаем исключение после всех попыток
        }

        verify(hashRepository, atLeast(1)).getUniqueNumbers(5);
        verify(encoder, atLeast(1)).encode(uniqueNumbers);
        verify(hashRepository, atLeast(1)).save(hashes);
        verify(meterRegistry, never()).timer("hash.generation.duration");
        verify(meterRegistry).counter("hash.generation.error", "exception", "CompletionException");
        verify(errorCounter).increment();
    }
}

