package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.local_cache.LocalCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlServiceValidator urlServiceValidator;

    @Mock
    private LocalCache localCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private String link = "http://localhost:8080/api/v1/shorter/redirect/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "link", link);
    }

    @Test
    public void testCreateNewShortUrl() throws Exception {
        URL originalUrl = new URL("http://original.url");
        String hash = "abc123";
        when(localCache.getCache()).thenReturn(hash);

        URL shortUrl = urlService.createNewShortUrl(originalUrl);

        assertEquals(new URL(link + hash), shortUrl);
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    public void testCreateNewShortUrlExistDb() throws Exception {
        URL originalUrl = new URL("http://original.url");
        String hash = "abc123";

        Url url = new Url();
        url.setUrl(originalUrl);
        url.setHash(hash);

        when(urlRepository.findByUrl(originalUrl)).thenReturn(Optional.of(url));

        URL shortUrl = urlService.createNewShortUrl(originalUrl);

        assertEquals(new URL(link + hash), shortUrl);
        verify(urlRepository, times(0)).save(any(Url.class));
        verify(urlCacheRepository, times(0)).saveAtRedis(any(String.class), any(URL.class));
    }

    @Test
    public void testGetUrlFromDb() throws MalformedURLException {
        String hash = "abc123";
        URL expectedUrl = new URL("http://original.url");

        Url url = new Url();
        url.setUrl(expectedUrl);
        url.setHash(hash);

        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        URL actualUrl = urlService.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlRepository, times(1)).findByHash(hash);
    }

    @Test
    public void testGetUrlFromDb_UrlNotFound() {
        String hash = "nonexistent_hash";
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(hash));

        verify(urlRepository, times(1)).findByHash(hash);
    }
}