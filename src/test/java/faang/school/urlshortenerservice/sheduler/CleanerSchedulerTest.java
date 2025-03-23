package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void cleanExpiredUrls() {
        List<String> hashesAsStrings = List.of("Hash");
        Mockito.when(urlRepository.removeExpiredUrlsAndGetHashes()).thenReturn(List.of("Hash"));

        List<Hash> hashes = hashesAsStrings.stream()
                .map(Hash::new)
                .toList();

        cleanerScheduler.cleanAndSaveExpiredHashes();
        Mockito.verify(hashRepository).saveAll(hashes);
    }
}
