package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.SaveDbJdbc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private SaveDbJdbc saveDbJdbc;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void testCleanerScheduler() {
        List<String> freeHashes = List.of("101", "102", "103");
        when(urlRepository.removeOldUrls()).thenReturn(freeHashes);
        doNothing().when(saveDbJdbc).save(freeHashes);

        cleanerScheduler.removeOldUrl();

        verify(urlRepository, times(1)).removeOldUrls();
        verify(saveDbJdbc, times(1)).save(freeHashes);
    }
}