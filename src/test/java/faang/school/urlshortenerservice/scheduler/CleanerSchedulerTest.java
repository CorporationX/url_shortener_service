package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;
    @Mock
    private  UrlRepository urlRepository;
    @Mock
    private  HashRepository hashRepository;

    private final int daysAgo = 2;
    private final List<String> oldHashes = List.of("qwe", "rty");

    @Test
    public void testClearOldHashesWithOldHashes(){
        ReflectionTestUtils.setField(cleanerScheduler, "daysAgo", daysAgo);
        when(urlRepository.clearOldHashes(daysAgo)).thenReturn(oldHashes);

        cleanerScheduler.clearOldHashes();

        InOrder inOrder = Mockito.inOrder(urlRepository, hashRepository);
        inOrder.verify(urlRepository, times(1)).clearOldHashes(daysAgo);
        inOrder.verify(hashRepository, times(1)).saveAll(oldHashes);
    }

    @Test
    public void testClearOldHashesWithoutOldHashes(){
        ReflectionTestUtils.setField(cleanerScheduler, "daysAgo", daysAgo);
        when(urlRepository.clearOldHashes(daysAgo)).thenReturn(Collections.emptyList());

        cleanerScheduler.clearOldHashes();

        InOrder inOrder = Mockito.inOrder(urlRepository, hashRepository);
        inOrder.verify(urlRepository, times(1)).clearOldHashes(daysAgo);
        inOrder.verifyNoMoreInteractions();
    }
}
