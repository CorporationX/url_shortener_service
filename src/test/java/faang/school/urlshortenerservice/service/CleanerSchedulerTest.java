package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void cleanOldUrls_shouldDeleteOldUrlsAndFreeHashes() {
        List<Url> oldUrls = List.of(new Url("hash1", "http://example.com", LocalDateTime.now().minusYears(2)));
        List<String> freedHashes = List.of("hash1");

        when(urlRepository.findAllByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(oldUrls);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteAll(oldUrls);
        verify(hashRepository).saveAll(argThat((List<Hash> hashesList) -> hashesList.size() == freedHashes.size()));
        verify(urlCacheRepository).delete("hash1");
    }
}