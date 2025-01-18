package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Value("${scheduler.year}")
    private int year;

    @Test
    void removeOldUrls_positive() {
        List<String> oldHashes = List.of("hash1", "hash2");
        when(urlRepository.removeOldLinks(any(LocalDateTime.class))).thenReturn(oldHashes);

        cleanerScheduler.removeOldUrls();

        verify(urlRepository, times(1)).removeOldLinks(any(LocalDateTime.class));
        verify(hashRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void removeOldUrls_negative() {
        when(urlRepository.removeOldLinks(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        cleanerScheduler.removeOldUrls();

        verify(urlRepository, times(1)).removeOldLinks(any(LocalDateTime.class));
        verifyNoInteractions(hashRepository);
    }
}