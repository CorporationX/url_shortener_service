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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    private final int DAYS_PASSED_FOR_REMOVE = 365;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(cleanerScheduler, "clearOldHashTime", DAYS_PASSED_FOR_REMOVE);
    }

    @Test
    public void clearOldHash() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.deleteRecordsAndReturnHash(any())).thenReturn(hashes);

        cleanerScheduler.clearOldHash();

        verify(hashRepository, times(1)).save(hashes);
    }
}
