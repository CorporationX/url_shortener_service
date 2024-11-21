package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.ShortLinkHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private ShortLinkHashRepository hashRepository;
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void scheduledCleanUrlsTest() {
        List<Hash> listHashes = List.of(new Hash("abra"), new Hash("cada"), new Hash("bra"));
        Mockito.when(urlRepository.deleteOneYearUrl()).thenReturn(listHashes);
        cleanerScheduler.scheduledCleanUrls();
        Mockito.verify(urlRepository, Mockito.times(1)).deleteOneYearUrl();
        Mockito.verify(hashRepository, Mockito.times(1)).saveAll(listHashes);
    }
}