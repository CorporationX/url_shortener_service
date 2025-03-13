package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashService hashService;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void cleanOldUrls_WithFreedHashes_ShouldReinsertHashes() {
        ReflectionTestUtils.setField(cleanerScheduler, "batchSize", 10000);

        List<String> freedHashes = Arrays.asList("hash1", "hash2");
        when(urlRepository.deleteOldUrls(any(LocalDateTime.class))).thenReturn(freedHashes);

        cleanerScheduler.cleanOldUrls();
        verify(hashService, times(1)).saveHashes(freedHashes);
    }

    @Test
    public void cleanOldUrls_WithNoFreedHashes_ShouldNotCallSaveAll() {
        when(urlRepository.deleteOldUrls(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        cleanerScheduler.cleanOldUrls();
        verify(hashService, never()).saveHashes(any());
    }
}
