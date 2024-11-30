package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerServiceTest {

    private static final int DELETION_PERIOD_DAYS = 365;
    private List<Url> urls;
    private List<Hash> hashes;
    private List<String> hashStrings;
    private CleanerService cleanerService;
    private LocalDateTime timestamp;

    @Captor
    private ArgumentCaptor<List<Hash>> urlCaptor;

    @Captor
    private ArgumentCaptor<String> hashStringCaptor;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @BeforeEach
    public void setup() {
        cleanerService = new CleanerService(
            DELETION_PERIOD_DAYS,
            urlRepository,
            hashRepository,
            urlCacheRepository
        );
        timestamp = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(DELETION_PERIOD_DAYS);
        urls = new ArrayList<>(Arrays.asList(
            createUrl("https:11", "11"),
            createUrl("https:22", "22"),
            createUrl("https:33", "33")
        ));
        hashes = new ArrayList<>(Arrays.asList(
            createHash("11"),
            createHash("22"),
            createHash("33")
        ));

        hashStrings = new ArrayList<>(Arrays.asList(
            "11",
            "22",
            "33"
        ));
    }

    @Test
    public void testClean_SaveInDB() {
        // Arrange
        when(urlRepository.deleteExpiredUrl(timestamp)).thenReturn(urls);

        // Act
        cleanerService.clean();
        verify(hashRepository, times(1)).saveAll(urlCaptor.capture());

        // Assert
        assertEquals(hashes, urlCaptor.getValue());
    }

    @Test
    public void testClean_ClearCache() {
        // Arrange
        when(urlRepository.deleteExpiredUrl(timestamp)).thenReturn(urls);

        // Act
        cleanerService.clean();
        verify(urlCacheRepository, times(hashStrings.size())).clearCache(hashStringCaptor.capture());

        // Assert
        assertEquals(hashStrings, hashStringCaptor.getAllValues());
    }

    private Url createUrl(String url, String hash) {
        return Url.builder()
            .url(url)
            .hash(hash)
            .build();
    }

    private Hash createHash(String hash) {
        return Hash.builder()
            .hash(hash)
            .build();
    }
}
