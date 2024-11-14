package faang.school.urlshortenerservice.scheduler.cleanHash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Test
    public void testCleaner() {
        List<Hash> hashes = List.of(new Hash("exampleHash1", null), new Hash("exampleHash2", null));
        when(urlRepository.deleteOldUrlsAndReturnHashesAsHashEntities()).thenReturn(hashes);

        cleanerScheduler.cleaner();

        verify(hashRepository, times(1)).saveAll(hashes);

        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashesAsHashEntities();
    }
}