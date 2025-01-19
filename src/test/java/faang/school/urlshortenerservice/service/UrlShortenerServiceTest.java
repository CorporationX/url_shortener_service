package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Test
    void testShortUrl() {
        String hash = "a4f";
        Mockito.when(hashCache.getHash()).thenReturn("a4f");
        Mockito.when(urlRepository.save(any())).thenAnswer(a -> a.getArgument(0));
        Mockito.doNothing().when(urlCacheRepository).save(any(), any());

        UrlDto urlDto = UrlDto.builder()
                .url("http://localhost:80/test")
                .build();
        HashDto hashDto = urlShortenerService.shortUrl(urlDto);
        String actualHash = hashDto.getHash();

        assertEquals(hash, actualHash);
    }

    @Test
    void testGetUrlByHash_CacheFound() {
        String hash = "4fa";
        String url = "http://localhost:8080/test";
        Mockito.when(urlCacheRepository.get(hash)).thenReturn(Optional.of(url));

        String actual = urlShortenerService.getUrlByHash(hash);
        assertEquals(url, actual);
        Mockito.verify(urlCacheRepository, times(1)).get(any());
        Mockito.verify(urlRepository, times(0)).findByHash(any());
    }

    @Test
    void testGetUrlByHash_DbFound() {
        String hash = "4fa";
        String url = "http://localhost:8080/test";
        Url savedUrl = Url.builder()
                .id(1L)
                .hash(hash)
                .url(url)
                .build();
        Mockito.when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        Mockito.when(urlRepository.findByHash(hash)).thenReturn(Optional.of(savedUrl));

        String actual = urlShortenerService.getUrlByHash(hash);
        assertEquals(url, actual);
        Mockito.verify(urlCacheRepository, times(1)).get(any());
        Mockito.verify(urlRepository, times(1)).findByHash(any());
    }

    @Test
    void testGetUrlByHash_NotFound() {
        String hash = "4fa";
        Mockito.when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        Mockito.when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlShortenerService.getUrlByHash(hash));
        Mockito.verify(urlCacheRepository, times(1)).get(any());
        Mockito.verify(urlRepository, times(1)).findByHash(any());
    }
}
