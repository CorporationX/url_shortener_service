package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    private static final int DAYS_PASSED_FOR_REMOVE = 365;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(cleanerScheduler, "daysPassedForRemove", DAYS_PASSED_FOR_REMOVE);
    }

    @Test
    void testFreeHashes() {
        List<String> freedHashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.removeOld(any())).thenReturn(freedHashes);

        cleanerScheduler.freeHashes();

        verify(hashRepository).save(freedHashes);
    }
}