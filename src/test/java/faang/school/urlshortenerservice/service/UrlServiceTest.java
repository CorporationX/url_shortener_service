package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlReq;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.RedisCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private RedisCashRepository redisCashRepository;
    @InjectMocks
    private UrlService urlService;

    @Test
    void createShortUrlSuccessTest() {
        String url = "https://lol";
        String hash = "kek";
        UrlReq urlReq = new UrlReq(url);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.existsByUrl(url)).thenReturn(false);
        assertDoesNotThrow(() -> {
            String result = urlService.createShortUrl(urlReq);
            assertEquals("kek", result);
        });
        verify(hashCache).getHash();
        verify(urlRepository).existsByUrl(url);
        verify(urlRepository).save(Url.builder().hash(hash).url(url).build());
        verify(redisCashRepository).save(hash, url);
    }

    @Test
    void createShortUrlWithUrlAlreadyExistFailTest() {
        String url = "https://lol";
        String hash = "kek";
        UrlReq urlReq = new UrlReq(url);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.existsByUrl(url)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> urlService.createShortUrl(urlReq));
        verify(hashCache).getHash();
        verify(urlRepository).existsByUrl(url);
        verify(urlRepository, never()).save(Url.builder().hash(hash).url(url).build());
        verify(redisCashRepository, never()).save(hash, url);
    }
}
