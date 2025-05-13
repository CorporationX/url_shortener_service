package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.UrlCache;
import faang.school.urlshortenerservice.service.cache.HashCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCache urlCache;
    @Mock
    private HashCache hashCache;
    @InjectMocks
    private UrlService urlService;

    @Test
    public void getLongUrl_ShouldReturnUrlFromCache() {
        String hash = "abc123";
        String longUrl = "https://www.example.com/test";
        when(urlCache.getLongUrl(hash)).thenReturn(longUrl);

        String result = urlService.getLongUrl(hash);
        assertThat(result).isEqualTo(longUrl);
        verify(urlCache, times(1)).getLongUrl(hash);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    public void getLongUrl_ShouldReturnUrlFromDatabaseAndUpdateCache() {
        String hash = "abc123";
        String longUrl = "https://www.example.com/test";
        when(urlCache.getLongUrl(hash)).thenReturn(null);
        Url urlEntity = Url.builder().hash(hash).url(longUrl).build();
        when(urlRepository.findByHash(hash)).thenReturn(urlEntity);

        String result = urlService.getLongUrl(hash);
        assertThat(result).isEqualTo(longUrl);
        verify(urlCache, times(1)).getLongUrl(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlCache, times(1)).saveUrlMapping(hash, longUrl);
    }

    @Test
    public void getLongUrl_ShouldReturnNotFound() {
        String hash = "nonexistent";
        when(urlCache.getLongUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(null);

        assertThatThrownBy(() -> urlService.getLongUrl(hash))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("No URL found for hash");
    }

    @Test
    public void createShortUrl_ShouldReturnShortUrlAndPersistMapping() {
        String longUrl = "https://www.example.com/long-url";
        String hash = "abc123";

        ReflectionTestUtils.setField(urlService, "baseShortUrl", "http://short.ly/");
        when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.createShortUrl(longUrl);
        assertThat(shortUrl).isEqualTo("http://short.ly/" + hash);
        verify(urlRepository, times(1))
                .save(argThat(url -> hash.equals(url.getHash()) && longUrl.equals(url.getUrl())));
        verify(urlCache, times(1)).saveUrlMapping(hash, longUrl);
    }
}
