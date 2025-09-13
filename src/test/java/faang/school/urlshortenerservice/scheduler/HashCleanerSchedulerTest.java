package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashCleanerScheduler scheduler;

    private final int expireAfterDays = 30;
    private final int saveBatchSize = 10;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(scheduler, "expireAfterDays", expireAfterDays);
        ReflectionTestUtils.setField(scheduler, "saveBatchSize", saveBatchSize);
    }

    @Test
    void releaseOldHashes_noExpiredHashes_nothingSaved() {
        when(urlRepository.getHashesOlderThanAndDelete(expireAfterDays))
                .thenReturn(List.of());

        scheduler.releaseOldHashes();

        verify(urlRepository).getHashesOlderThanAndDelete(expireAfterDays);
        verifyNoInteractions(hashRepository);
    }

    @Test
    void releaseOldHashes_expiredHashesAreSaved() {
        List<String> expired = List.of("h1", "h2", "h3");
        when(urlRepository.getHashesOlderThanAndDelete(expireAfterDays)).thenReturn(expired);

        scheduler.releaseOldHashes();

        verify(urlRepository).getHashesOlderThanAndDelete(expireAfterDays);
        verify(hashRepository).saveAllBatched(
                argThat(list -> list.size() == expired.size()),
                eq(saveBatchSize)
        );
    }
}