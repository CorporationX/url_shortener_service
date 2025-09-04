package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {
    @InjectMocks
    private CleanerScheduler scheduler;

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlCache urlCache;

    @Test
    void testClearExistsExpiredUrl() {
        List<String> strings = new ArrayList<>(List.of("1", "2"));
        when(urlRepository.deleteUrlBeforeCreatedAt(any(), anyInt()))
                .thenReturn(strings);

        scheduler.cleanUpExpiredUrls();

        verify(urlRepository, times(1))
                .deleteUrlBeforeCreatedAt(any(), anyInt());
        verify(hashRepository, times(1))
                .saveAll(any());
        verify(urlCache, times(1))
                .deleteAll(any());

    }

    @Test
    void testClearNonExistsExpiredUrl() {
        List<String> strings = new ArrayList<>();
        when(urlRepository.deleteUrlBeforeCreatedAt(any(), anyInt()))
                .thenReturn(strings);

        scheduler.cleanUpExpiredUrls();

        verify(urlRepository, times(1))
                .deleteUrlBeforeCreatedAt(any(), anyInt());
        verify(hashRepository, never())
                .saveAll(any());
        verify(urlCache, never())
                .deleteAll(any());
    }
}
