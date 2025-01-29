package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    private int deleteLimit = 100;

    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        cleanerScheduler = new CleanerScheduler(urlRepository, hashRepository, deleteLimit);
    }

    @Test
    void testCleanHashes() {
        Mockito.when(urlRepository.deleteAndGetHashes(anyInt())).thenReturn(List.of("af4"));
        Mockito.when(hashRepository.saveAll(any())).thenAnswer(a -> a.getArgument(0));

        Assertions.assertDoesNotThrow(()-> cleanerScheduler.cleanHashes());
        Mockito.verify(urlRepository, times(1)).deleteAndGetHashes(deleteLimit);
        Mockito.verify(hashRepository, times(1)).saveAll(any());
    }
}
