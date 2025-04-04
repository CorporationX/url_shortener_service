package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void cleanExpiredUrls() {
        Mockito.when(urlRepository.removeExpiredUrlsAndGetHashes()).thenReturn(List.of("Hash"));

        cleanerScheduler.cleanAndSaveExpiredHashes();

        Mockito.verify(hashRepository).saveAll(Mockito.anyList());
    }
}
