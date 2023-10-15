package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.url.ShortUrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepositoryMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlCacheRepositoryMock urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    private final String testOriginalURL = "https://www.example.com";
    private final String testHash = "abc123";
    private final String serverAddress = "https://sh.c/";
    private final String testCacheValue = "https://www.example.com/cached";
    private Url testUrl;


    @BeforeEach
    void setUp() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);

        testUrl = Url.builder()
                .url(testOriginalURL)
                .createdAt(createdAt)
                .build();

        ReflectionTestUtils.setField(urlService, "serverAddress", serverAddress);
    }

    @Test
    void testShortenUrl() {
        when(hashCache.getHash()).thenReturn(new Hash(testHash));

        String result = urlService.shortenUrl(testOriginalURL);
        String expected = serverAddress + testHash;

        assertEquals(expected, result);
    }


    @Test
    void testGetOriginalURLFromCache() {
        when(urlCacheRepository.get(testHash)).thenReturn(testCacheValue);

        String originalURL = urlService.getOriginalURL(testHash);

        assertEquals(testCacheValue, originalURL);
    }

    @Test
    void testGetOriginalURLFromRepository() {
        when(urlRepository.findByHash(testHash)).thenReturn(testUrl);

        String originalURL = urlService.getOriginalURL(testHash);

        assertEquals(testOriginalURL, originalURL);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).save(testHash, testOriginalURL);
    }

    @Test
    void testGetOriginalURLNotFound() {
        when(urlRepository.findByHash(testHash)).thenReturn(null);
        when(urlCacheRepository.get(testHash)).thenReturn(null);

        assertThrows(ShortUrlNotFoundException.class, () -> urlService.getOriginalURL(testHash));
    }
}