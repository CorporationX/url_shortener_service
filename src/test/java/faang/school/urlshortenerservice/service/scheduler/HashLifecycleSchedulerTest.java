package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashLifecycleSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private FreeHashRepository freeHashRepository;

    @InjectMocks
    private HashLifecycleScheduler hashLifecycleScheduler;

    @Test
    void cleanUpAndFreeHashes_shouldMoveExpiredUrlsToFreeHash() {
        LocalDateTime now = LocalDateTime.now();
        UrlMapping expiredMapping = new UrlMapping(
                "abc123",
                "https://expired.com",
                now.minusDays(1),
                now.minusDays(1).plusMinutes(10));

        List<UrlMapping> expiredMappings = Arrays.asList(expiredMapping);

        when(urlRepository.findByExpiredAtBefore(any())).thenReturn(expiredMappings);

        hashLifecycleScheduler.cleanUpAndFreeHashes();

        verify(urlRepository).deleteAll(expiredMappings);
        verify(freeHashRepository).saveAll(anyList());
    }

    @Test
    void cleanUpAndFreeHashes_shouldDoNothingWhenNoExpiredUrls() {
        List<UrlMapping> expiredMappings = Arrays.asList();

        when(urlRepository.findByExpiredAtBefore(any())).thenReturn(expiredMappings);

        hashLifecycleScheduler.cleanUpAndFreeHashes();

        verify(urlRepository, never()).deleteAll(anyList());
        verify(freeHashRepository, never()).saveAll(anyList());
    }
}