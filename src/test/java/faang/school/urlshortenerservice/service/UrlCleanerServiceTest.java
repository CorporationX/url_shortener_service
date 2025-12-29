package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UrlCleanerServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private UrlCleanerService urlCleanerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlCleanerService, "retentionYears", 1);
        ReflectionTestUtils.setField(urlCleanerService, "batchSize", 1000);
        ReflectionTestUtils.setField(urlCleanerService, "maxRetryAttempts", 3);
        ReflectionTestUtils.setField(urlCleanerService, "retryDelayMs", 100L);
        ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", false);
    }

    @Test
    void cleanOldUrls_WhenNoOldUrls_ShouldDoNothing() {
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(Collections.emptyList());

        urlCleanerService.cleanOldUrls();

        verify(urlRepository, times(1)).getOldUrlHashesBatch(any(LocalDateTime.class), anyInt());
        verify(hashRepository, never()).save(anyList());
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void cleanOldUrls_WhenSingleBatch_ShouldProcessSuccessfully() {
        List<String> batchHashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(batchHashes)
                .thenReturn(Collections.emptyList());

        urlCleanerService.cleanOldUrls();

        verify(urlRepository, times(2)).getOldUrlHashesBatch(any(LocalDateTime.class), anyInt());
        verify(hashRepository, times(1)).save(batchHashes);
        verify(urlRepository, times(1)).deleteUrlsByHashes(batchHashes);
        verify(hashRepository, never()).delete(anyList());
    }

    @Test
    void cleanOldUrls_WhenMultipleBatches_ShouldProcessAll() {
        List<String> firstBatch = List.of("hash1", "hash2");
        List<String> secondBatch = List.of("hash3", "hash4");
        List<String> thirdBatch = List.of("hash5");

        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(firstBatch)
                .thenReturn(secondBatch)
                .thenReturn(thirdBatch)
                .thenReturn(Collections.emptyList());

        urlCleanerService.cleanOldUrls();

        verify(urlRepository, times(4)).getOldUrlHashesBatch(any(LocalDateTime.class), anyInt());
        verify(hashRepository, times(1)).save(firstBatch);
        verify(hashRepository, times(1)).save(secondBatch);
        verify(hashRepository, times(1)).save(thirdBatch);
        verify(urlRepository, times(1)).deleteUrlsByHashes(firstBatch);
        verify(urlRepository, times(1)).deleteUrlsByHashes(secondBatch);
        verify(urlRepository, times(1)).deleteUrlsByHashes(thirdBatch);
    }

    @Test
    void cleanOldUrls_WhenShutdownDuringProcessing_ShouldStopGracefully() {
        List<String> batchHashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenAnswer(invocation -> {
                    ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", true);
                    return batchHashes;
                });

        urlCleanerService.cleanOldUrls();

        verify(urlRepository, times(1)).getOldUrlHashesBatch(any(LocalDateTime.class), anyInt());
        verify(hashRepository, never()).save(anyList());
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void cleanOldUrls_WhenShutdownAfterSavingHashes_ShouldCompensate() {
        List<String> batchHashes = List.of("hash1", "hash2");
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(batchHashes)
                .thenReturn(Collections.emptyList());

        doAnswer(invocation -> {
            ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", true);
            return null;
        }).when(hashRepository).save(anyList());

        urlCleanerService.cleanOldUrls();

        verify(hashRepository, times(1)).save(batchHashes);
        verify(hashRepository, times(1)).delete(batchHashes);
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void cleanOldUrls_WhenErrorOccurs_ShouldRetry() {
        List<String> batchHashes = List.of("hash1", "hash2");
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(batchHashes)
                .thenReturn(Collections.emptyList());

        doThrow(new RuntimeException("Database error"))
                .doNothing()
                .when(hashRepository).save(anyList());

        urlCleanerService.cleanOldUrls();

        verify(hashRepository, times(2)).save(batchHashes);
        verify(urlRepository, times(1)).deleteUrlsByHashes(batchHashes);
    }

    @Test
    void cleanOldUrls_WhenErrorAfterAllRetries_ShouldThrowException() {
        List<String> batchHashes = List.of("hash1", "hash2");
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(batchHashes);

        doThrow(new RuntimeException("Database error"))
                .when(hashRepository).save(anyList());

        assertThatThrownBy(() -> urlCleanerService.cleanOldUrls())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        verify(hashRepository, times(3)).save(batchHashes);
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void cleanOldUrls_WhenNullBatchReturned_ShouldHandleGracefully() {
        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(null);

        urlCleanerService.cleanOldUrls();

        verify(urlRepository, times(1)).getOldUrlHashesBatch(any(LocalDateTime.class), anyInt());
        verify(hashRepository, never()).save(anyList());
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void onShutdown_ShouldSetShuttingDownFlag() {
        assertThat(ReflectionTestUtils.getField(urlCleanerService, "shuttingDown"))
                .isEqualTo(false);

        urlCleanerService.onShutdown();

        assertThat(ReflectionTestUtils.getField(urlCleanerService, "shuttingDown"))
                .isEqualTo(true);
    }

    @Test
    void processBatch_WhenShutdownBeforeSave_ShouldReturnZero() {
        List<String> hashes = List.of("hash1", "hash2");
        ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", true);

        int result = (int) ReflectionTestUtils.invokeMethod(urlCleanerService, "processBatch", hashes);

        assertThat(result).isEqualTo(0);
        verify(hashRepository, never()).save(anyList());
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void processBatch_WhenShutdownAfterSave_ShouldCompensate() {
        List<String> hashes = List.of("hash1", "hash2");
        ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", false);

        doAnswer(invocation -> {
            ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", true);
            return null;
        }).when(hashRepository).save(anyList());

        int result = (int) ReflectionTestUtils.invokeMethod(urlCleanerService, "processBatch", hashes);

        assertThat(result).isEqualTo(0);
        verify(hashRepository, times(1)).save(hashes);
        verify(hashRepository, times(1)).delete(hashes);
        verify(urlRepository, never()).deleteUrlsByHashes(anyList());
    }

    @Test
    void processBatch_WhenSuccessful_ShouldReturnProcessedCount() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        ReflectionTestUtils.setField(urlCleanerService, "shuttingDown", false);

        int result = (int) ReflectionTestUtils.invokeMethod(urlCleanerService, "processBatch", hashes);

        assertThat(result).isEqualTo(3);
        verify(hashRepository, times(1)).save(hashes);
        verify(urlRepository, times(1)).deleteUrlsByHashes(hashes);
        verify(hashRepository, never()).delete(anyList());
    }

    @Test
    void compensateSaveHashes_WhenSuccessful_ShouldDeleteHashes() {
        List<String> hashes = List.of("hash1", "hash2");

        ReflectionTestUtils.invokeMethod(urlCleanerService, "compensateSaveHashes", hashes);

        verify(hashRepository, times(1)).delete(hashes);
    }

    @Test
    void compensateSaveHashes_WhenErrorOccurs_ShouldLogError() {
        List<String> hashes = List.of("hash1", "hash2");
        doThrow(new RuntimeException("Delete error"))
                .when(hashRepository).delete(anyList());

        ReflectionTestUtils.invokeMethod(urlCleanerService, "compensateSaveHashes", hashes);

        verify(hashRepository, times(1)).delete(hashes);
    }

    @Test
    void cleanOldUrls_WhenLargeBatch_ShouldProcessCorrectly() {
        List<String> largeBatch = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            largeBatch.add("hash" + i);
        }

        when(urlRepository.getOldUrlHashesBatch(any(LocalDateTime.class), anyInt()))
                .thenReturn(largeBatch)
                .thenReturn(Collections.emptyList());

        urlCleanerService.cleanOldUrls();

        verify(hashRepository, times(1)).save(largeBatch);
        verify(urlRepository, times(1)).deleteUrlsByHashes(largeBatch);
    }
}

