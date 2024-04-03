package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    public void setUp() {
        cleanerScheduler = new CleanerScheduler(urlRepository, hashRepository);
    }

    @Test
    public void testCleanOldHashesWhenUrlsExistThenHashesSaved() {
        List<String> urlHashes = Arrays.asList("hash1", "hash2", "hash3");
        List<Hash> hashes = Arrays.asList(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));

        when(urlRepository.deleteUrl(any())).thenReturn(urlHashes);
        when(hashRepository.saveAll(hashes)).thenReturn(hashes);

        cleanerScheduler.cleanOldHashes();

        verify(urlRepository, times(1)).deleteUrl(any());
        verify(hashRepository, times(1)).saveAll(hashes);
    }
}