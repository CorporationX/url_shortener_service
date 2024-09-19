package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlRepository urlRepository;
    private CleanerScheduler cleanerScheduler;
    private int expirationDays;
    List<String> expiredHashes;
    List<Hash> reusedHashes;

    @BeforeEach
    void setUp() {
        expirationDays = 2;
        cleanerScheduler = new CleanerScheduler(expirationDays, hashRepository, urlRepository);
        expiredHashes = List.of("a", "b", "c", "d");
        reusedHashes = List.of(new Hash("a"), new Hash("b"), new Hash("c"), new Hash("d"));
    }

    @Test
    void testClean() {
        when(urlRepository.deleteAndGetExpiredHashes(expirationDays)).thenReturn(expiredHashes);
        cleanerScheduler.clean();
        verify(urlRepository, times(1)).deleteAndGetExpiredHashes(expirationDays);
        verify(hashRepository, times(1)).saveAll(reusedHashes);
    }
}