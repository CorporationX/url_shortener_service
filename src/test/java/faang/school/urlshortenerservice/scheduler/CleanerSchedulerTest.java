package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    List<String> expiredHashes = List.of("abc123", "xyz789");

    @Test
    void testScheduleCleanerWithNoExpiredUrlsDoesNotSaveHashes() {
        when(urlRepository.deleteUrlsOlderThanOneYear()).thenReturn(List.of());

        cleanerScheduler.cleanOldUrlsAndRestoreHashes();

        verify(urlRepository, times(1)).deleteUrlsOlderThanOneYear();
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void testScheduleCleanerWithExpiredUrlsSavesHashes() {
        when(urlRepository.deleteUrlsOlderThanOneYear()).thenReturn(expiredHashes);
        when(hashRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        cleanerScheduler.cleanOldUrlsAndRestoreHashes();

        verify(urlRepository, times(1)).deleteUrlsOlderThanOneYear();
        verify(hashRepository, times(1)).saveAll(any());
    }

    @Test
    void testScheduleCleanerRestoresCorrectHashEntities() {
        when(urlRepository.deleteUrlsOlderThanOneYear()).thenReturn(expiredHashes);

        cleanerScheduler.cleanOldUrlsAndRestoreHashes();

        ArgumentCaptor<List<Hash>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository, times(1)).saveAll(captor.capture());

        List<Hash> capturedHashes = captor.getValue();
        List<String> actualHashes = capturedHashes.stream().map(Hash::getHash).collect(Collectors.toList());

        assertEquals(expiredHashes, actualHashes);
        assertEquals(capturedHashes.size(), expiredHashes.size());
    }
}