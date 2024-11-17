package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.WrongUrl;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashProperties hashProperties;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlShortenerProperties urlShortenerProperties;

    @InjectMocks
    private UrlService urlService;

    private final String validUrl = "https://example.com";
    private final String invalidUrl = "htp://bad-url";
    private final String hash = "123abc";
    private final String protocol = "https";
    private final String domain = "sh.c";


    @Test
    void testShorten_ValidUrl() {
        when(urlShortenerProperties.getProtocol()).thenReturn(protocol);
        when(urlShortenerProperties.getDomain()).thenReturn(domain);
        when(hashCache.getHash()).thenReturn(hash);

        String expectedShortUrl = "https://sh.c/" + hash;
        String result = urlService.shorten(validUrl);

        assertEquals(expectedShortUrl, result);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
        verify(urlCacheRepository, times(1)).put(hash, validUrl);
    }

    @Test
    void testShorten_InvalidUrl() {
        assertThrows(WrongUrl.class, () -> urlService.shorten(invalidUrl));
    }

    @Test
    void testGetUrl_FromCache() {
        when(urlCacheRepository.get(hash)).thenReturn(validUrl);

        String result = urlService.getUrl(hash);

        assertEquals(validUrl, result);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, never()).findById(hash);
    }

    @Test
    void testGetUrl_FromDatabase() {
        when(urlCacheRepository.get(hash)).thenReturn(null);

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setUrl(validUrl);
        urlEntity.setHash(hash);

        when(urlRepository.findById(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getUrl(hash);

        assertEquals(validUrl, result);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void testGetUrl_NotFoundInDatabase() {
        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> urlService.getUrl(hash));
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void testCleanHashes() {
        long daysToKeep = 365L;
        when(urlShortenerProperties.getDaysToKeep()).thenReturn(daysToKeep);

        UrlEntity urlEntity1 = new UrlEntity();
        urlEntity1.setHash("aaaaaa");
        UrlEntity urlEntity2 = new UrlEntity();
        urlEntity2.setHash("bbbbbb");

        List<UrlEntity> urlEntities = List.of(urlEntity1, urlEntity2);
        when(urlRepository.deleteByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(urlEntities);

        urlService.cleanHashes();

        verify(urlShortenerProperties, times(1)).getDaysToKeep();
        verify(urlRepository, times(1)).deleteByCreatedAtBefore(any(LocalDateTime.class));
        verify(hashRepository, times(1)).save(List.of("aaaaaa", "bbbbbb"));
    }
}
