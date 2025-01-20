package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.CleanerSchedulerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    private CleanerSchedulerProperties properties;

    @BeforeEach
    void setUp() {
        properties = new CleanerSchedulerProperties();
        properties.setCron("0 0 1 * * ?");
    }

    @Test
    void testCleanUp_NoFreedHashes() {
        when(urlRepository.deleteOlderThanOneYear()).thenReturn(List.of());

        cleanerScheduler.cleanUp();

        verify(urlRepository, times(1)).deleteOlderThanOneYear();
        verify(hashRepository, never()).save(anyList());
    }

    @Test
    void testCleanUp_WithFreedHashes() {
        when(urlRepository.deleteOlderThanOneYear()).thenReturn(List.of("hash1", "hash2", "hash3"));

        cleanerScheduler.cleanUp();

        verify(urlRepository, times(1)).deleteOlderThanOneYear();
        verify(hashRepository, times(1)).save(List.of("hash1", "hash2", "hash3"));
    }
}
