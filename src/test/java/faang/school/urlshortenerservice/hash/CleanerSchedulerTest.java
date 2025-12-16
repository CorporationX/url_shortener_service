package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private List<Url> emptyUrlList;

    private List<Url> anyNonEmptyUrlList;

    @BeforeEach
    public void setUp() throws Exception {
        int anyInt = 10;
        emptyUrlList = new ArrayList<>();
        anyNonEmptyUrlList = List.of(
                new Url("anyHash", "anyWebAddress"),
                new Url("anyHash2", "anyWebAddress2"));
        Field storageLifeInDays = CleanerScheduler.class.getDeclaredField("storageLifeInDays");
        storageLifeInDays.setAccessible(true);
        storageLifeInDays.set(cleanerScheduler, anyInt);
    }

    @Test
    public void deleteOldUrls_FoundZeroOldUrls() {
        when(urlRepository.getAndDeleteOldHashes(any(LocalDateTime.class))).thenReturn(emptyUrlList);

        cleanerScheduler.deleteOldUrls();

        verify(urlCacheRepository, never()).deleteAllById(any(Iterable.class));
    }

    @Test
    public void deleteOldUrls_DeletesOldUrls() {
        when(urlRepository.getAndDeleteOldHashes(any(LocalDateTime.class))).thenReturn(anyNonEmptyUrlList);

        cleanerScheduler.deleteOldUrls();

        verify(urlRepository, times(1)).getAndDeleteOldHashes(any(LocalDateTime.class));
        verify(urlCacheRepository, times(1)).deleteAllById(any(Iterable.class));
        verify(hashRepository, times(1)).saveAll(any(Iterable.class));
    }
}
