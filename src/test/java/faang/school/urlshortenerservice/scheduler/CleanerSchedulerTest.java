package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.db.HashRepository;
import faang.school.urlshortenerservice.repository.db.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    private final UrlRepository mockUrlRepository = mock(UrlRepository.class);
    private final HashRepository mockHashRepository = mock(HashRepository.class);
    private final String textPeriod = "P1Y";

    private final CleanerScheduler cleanerScheduler
            = new CleanerScheduler(mockUrlRepository, mockHashRepository, textPeriod);

    @Captor
    ArgumentCaptor<List<String>> hashesCaptor;

    @Test
    void releaseHashes() {
        List<String> hashes = List.of("1", "2");
        when(mockUrlRepository.pollBeforeStamp(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.releaseHashes();

        verify(mockUrlRepository, times(1)).pollBeforeStamp(any(LocalDateTime.class));
        verify(mockHashRepository, times(1)).saveBatch(hashesCaptor.capture());
        assertEquals(hashes, hashesCaptor.getValue());
    }
}