package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hashes.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(urlService, "protocol", "http");
        ReflectionTestUtils.setField(urlService, "host", "localhost");
        ReflectionTestUtils.setField(urlService, "port", 8080);
    }

    @Test
    public void getOriginalUrl_urlNotFound() {
        String hash = "hash";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        UrlNotFoundException urlNotFoundException =
                assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals(urlNotFoundException.getMessage(), "Url by this hash not found not found");

        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    public void getOriginalUrl_urlFoundInCache() {
        String hash = "hash";
        String url = "http://localhost:8080";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(url));

        String originalUrl = urlService.getOriginalUrl(hash);
        assertEquals(originalUrl, url);
        verify(urlCacheRepository, times(1)).get(hash);
    }

    @Test
    public void getOriginalUrl_urlFoundInDataSource() {
        String hash = "hash";
        String hashInCache = " ";
        String url = "http://localhost:8080";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(Url.builder().hash(hash).url(url).build()));

        String originalUrl = urlService.getOriginalUrl(hash);
        assertEquals(originalUrl, url);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    public void getShortUrl_successfully() throws MalformedURLException {
        String hash = "9b";
        URL urlRequest = new URL("https://example.com");
        URL urlResponse = new URL("http://localhost:8080/9b");

        when(hashCache.getHash()).thenReturn(hash);

        URL shortUrl = urlService.getShortUrl(urlRequest);
        assertEquals(shortUrl, urlResponse);
        verify(hashCache, times(1)).getHash();
    }

}