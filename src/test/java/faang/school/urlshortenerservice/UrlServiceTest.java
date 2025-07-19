package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repo.UrlCache;
import faang.school.urlshortenerservice.repo.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCache urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private final String testOriginalUrl = "https://faang-school.com/courses/";
    private final String testHash = "32dny9";
    private final Url testUrlEntity = Url.builder()
            .originalUrl(testOriginalUrl)
            .hash(testHash)
            .build();

    @Test
    void testCreateShortUrlSuccess() {

        when(hashCache.getHash()).thenReturn(testHash);
        when(urlRepository.save(any(Url.class))).thenReturn(testUrlEntity);

        String result = urlService.createShortUrl(testOriginalUrl);

        assertEquals(testHash, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(testHash, testOriginalUrl);
    }

    @Test
    void testGetOriginalUrlWhenUrlInCache() {

        when(urlCacheRepository.get(testHash)).thenReturn(testOriginalUrl);

        Optional<String> result = urlService.getOriginalUrl(testHash);

        assertTrue(result.isPresent());
        assertEquals(testOriginalUrl, result.get());
        verify(urlCacheRepository).get(testHash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetOriginalUrlWhenUrlInDatabase() {

        when(urlCacheRepository.get(testHash)).thenReturn(null);
        when(urlRepository.findByHash(testHash)).thenReturn(Optional.of(testUrlEntity));

        Optional<String> result = urlService.getOriginalUrl(testHash);

        assertTrue(result.isPresent());
        assertEquals(testOriginalUrl, result.get());
        verify(urlCacheRepository).get(testHash);
        verify(urlRepository).findByHash(testHash);
    }

    @Test
    void testGetOriginalUrlWhenUrlNotFound() {

        when(urlCacheRepository.get(testHash)).thenReturn(null);
        when(urlRepository.findByHash(testHash)).thenReturn(Optional.empty());

        Optional<String> result = urlService.getOriginalUrl(testHash);

        assertTrue(result.isEmpty());
        verify(urlCacheRepository).get(testHash);
        verify(urlRepository).findByHash(testHash);
    }

    @Test
    void testGetOriginalUrlWhenCacheReturnsEmptyString() {

        when(urlCacheRepository.get(testHash)).thenReturn("");

        Optional<String> result = urlService.getOriginalUrl(testHash);

        assertTrue(result.isPresent());
        assertEquals("", result.get());
        verify(urlCacheRepository).get(testHash);
        verifyNoInteractions(urlRepository);
    }
}