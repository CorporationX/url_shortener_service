package faang.school.urlshortenerservice.schedulertest;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.scheduler.CleanerScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler scheduler;

    private final String AGE_INTERVAL = "INTERVAL '1 year'";
    private final int BATCH_SIZE = 3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduler, "batchSize", BATCH_SIZE);
        ReflectionTestUtils.setField(scheduler, "ageIntervalSql", AGE_INTERVAL);
    }

    @Test
    void cleanOldUrls_nothingToDelete_noSave() {
        when(urlRepository.deleteOldReturningHashes(eq(AGE_INTERVAL), eq(BATCH_SIZE)))
                .thenReturn(emptyList());

        scheduler.cleanOldUrls();

        verify(urlRepository, times(1))
                .deleteOldReturningHashes(AGE_INTERVAL, BATCH_SIZE);

        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void cleanOldUrls_multipleBatches_savedAllHashes() {
        when(urlRepository.deleteOldReturningHashes(eq(AGE_INTERVAL), eq(BATCH_SIZE)))
                .thenReturn(List.of("h1", "h2", "h3"))
                .thenReturn(List.of("h4"))
                .thenReturn(emptyList());

        scheduler.cleanOldUrls();

        verify(urlRepository, atLeast(2))
                .deleteOldReturningHashes(AGE_INTERVAL, BATCH_SIZE);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Hash>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository, times(2)).saveAll(captor.capture());

        List<List<Hash>> all = captor.getAllValues();
        List<String> first = all.get(0).stream().map(Hash::getHash).toList();
        List<String> second = all.get(1).stream().map(Hash::getHash).toList();

        assertEquals(List.of("h1", "h2", "h3"), first);
        assertEquals(List.of("h4"), second);
    }
}
