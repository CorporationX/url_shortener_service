package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlCacheRepository, urlRepository, hashCache);
        ReflectionTestUtils.setField(urlService, "numberOfDaysForOutdatedHashes", 365);
    }

    @Test
    void generateShortUrl_shouldReturnSavedUrl_whenHashPresent() {
        String inputUrl = "http://example.com";
        String generatedHash = "abc123";

        when(hashCache.getNextHash()).thenReturn(generatedHash);

        Url savedUrl = Url.builder()
                .url(inputUrl)
                .hash(generatedHash)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(365))
                .build();
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        String result = urlService.generateShortUrl(inputUrl);

        assertTrue(StringUtils.hasText(result));
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).saveUrl(generatedHash, inputUrl);
    }

    @Test
    void generateShortUrl_shouldThrowException_whenHashNotPresent() {
        String inputUrl = "http://example.com";

        when(hashCache.getNextHash()).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> urlService.generateShortUrl(inputUrl));
        assertEquals("Failed to generate hash for URL", exception.getMessage());
    }

    @Test
    void getUrl_shouldReturnFromCache_whenResultPresent() {
        String hash = "hash1";
        String cachedUrl = "http://cached.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn(cachedUrl);

        String result = urlService.getUrl(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository, never()).saveUrl(any(), any());
    }

    @Test
    void getUrl_shouldQueryRepository_whenNotInCache() {
        String hash = "hash1";
        String repoUrl = "http://repository.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn("");

        Url url = Url.builder()
                .hash(hash)
                .url(repoUrl)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(365))
                .build();
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(hash);

        assertEquals(repoUrl, result);
        verify(urlCacheRepository).saveUrl(hash, repoUrl);
    }

    @Test
    void getUrl_shouldThrowUrlNotFoundException_whenNotFoundInCacheAndRepo() {
        String hash = "hash1";

        when(urlCacheRepository.getUrl(hash)).thenReturn("");
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getUrl(hash));
    }
}