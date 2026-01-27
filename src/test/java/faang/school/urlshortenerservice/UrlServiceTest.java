package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception_handler.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void createShortUrl_savesToReposAndReturnsShort() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        when(hashCache.getHash()).thenReturn(hash);

        // ВАЖНО: до вызова сервиса!
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://short.url/");

        String result = urlService.createShortUrl(longUrl);

        assertEquals("http://short.url/" + hash, result);
        verify(urlRepository).save(argThat(url -> url.getHash().equals(hash) && url.getUrl().equals(longUrl)));
        verify(urlCacheRepository).save(hash, longUrl);
    }



    @Test
    void findLongUrlByHash_foundInCache_returnsUrl() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        when(urlCacheRepository.findByHash(hash)).thenReturn(longUrl);

        String result = urlService.findLongUrlByHash(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void findLongUrlByHash_notInCacheButFoundInDb_returnsUrl() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(new Url(hash, longUrl)));

        String result = urlService.findLongUrlByHash(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    void findLongUrlByHash_notFound_throwsException() {
        String hash = "abc123";
        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.findLongUrlByHash(hash));
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }
}

