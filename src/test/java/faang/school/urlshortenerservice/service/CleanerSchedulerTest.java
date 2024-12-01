package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "lifeLinksOfDays", 365L);
    }

    @Test
    void cleanOldUrls_shouldDeleteOldUrlsAndFreeHashes() {
        List<String> freedHashes = List.of("hash1", "hash2");

        when(urlRepository.deleteAllByCreatedAtBeforeReturningHashes(any(LocalDateTime.class)))
                .thenReturn(freedHashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteAllByCreatedAtBeforeReturningHashes(any(LocalDateTime.class));
        verify(hashRepository).saveAll(argThat(hashes -> {
            List<Hash> hashList = StreamSupport.stream(hashes.spliterator(), false).toList();
            return hashList.size() == freedHashes.size() &&
                    hashList.stream().map(Hash::getHash).toList().containsAll(freedHashes);
        }));

        freedHashes.forEach(hash -> verify(urlCacheRepository).delete(hash));
    }
}