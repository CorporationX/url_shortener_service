package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "expiredTimeInYears", 1L);
    }

    @Test
    void cleanOldUrls_shouldClenOldUrls() {
        LocalDateTime expectedTime = LocalDateTime.now().minusYears(1L);
        Set<String> mockHashes = Set.of("hash1", "hash2");

        when(urlRepository.deleteByDateAndGetHashes(any(LocalDateTime.class))).thenReturn(mockHashes);

        cleanerScheduler.cleanOldUrls();

        InOrder inOrder = Mockito.inOrder(urlRepository, hashRepository);
        inOrder.verify(urlRepository).deleteByDateAndGetHashes(any(LocalDateTime.class));
        inOrder.verify(hashRepository).save(mockHashes);
    }

}