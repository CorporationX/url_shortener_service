package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void cleanOldUrls_removesOldUrlsAndSavesHashes() {
        when(urlRepository.removeOldAndGetHashes(any(LocalDateTime.class))).thenReturn(Arrays.asList("hash1", "hash2"));

        cleanerScheduler.cleanOldUrls();

        InOrder inOrder = inOrder(urlRepository, hashRepository);
        inOrder.verify(urlRepository, times(1)).removeOldAndGetHashes(any(LocalDateTime.class));
        inOrder.verify(hashRepository, times(1)).saveAll(Arrays.asList("hash1", "hash2"));
    }

    @Test
    void cleanOldUrls_doesNothingWhenNoOldUrlsFound() {
        when(urlRepository.removeOldAndGetHashes(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        cleanerScheduler.cleanOldUrls();

        InOrder inOrder = inOrder(urlRepository, hashRepository);
        inOrder.verify(urlRepository, times(1)).removeOldAndGetHashes(any(LocalDateTime.class));
        inOrder.verifyNoMoreInteractions();
    }
}