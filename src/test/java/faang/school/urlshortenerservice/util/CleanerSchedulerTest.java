package faang.school.urlshortenerservice.util;

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
class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void scheduledCleanTest() {
        List<String> freeHashes = List.of("hash1", "hash2", "hash3");
        Mockito.when(urlRepository.deleteOneYearUrl()).thenReturn(freeHashes);

        cleanerScheduler.scheduledClean();

        Mockito.verify(urlRepository, Mockito.times(1)).deleteOneYearUrl();
        Mockito.verify(hashRepository, Mockito.times(1)).saveAll(freeHashes);
    }
}