package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;

    @Test
    void testClean() {
        List<Url> oldHashes = List.of(new Url("Hash", "Url", LocalDateTime.MIN));
        List<Hash> hashes = List.of(new Hash("Hash"));

        when(urlRepository.getOldHashesAndDelete(any())).thenReturn(oldHashes);
        when(hashRepository.saveAll(hashes)).thenReturn(hashes);

        cleanerScheduler.clean();

        verify(urlRepository, times(1)).getOldHashesAndDelete(any());
        verify(hashRepository, times(1)).saveAll(hashes);
    }
}