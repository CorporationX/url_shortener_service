package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private RedisCacheRepository redisCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    private UrlDto urlDto;


    @BeforeEach
    void setUp() {
        urlDto = new UrlDto();
        urlDto.setUrl("http/localhost");
    }

    @Test
    void getShortUrl() {
        Mockito.when(hashCache.getHash()).thenReturn("abc123");

        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("http://example.com");

        String actual = urlService.getShortUrl(urlDto);

        Mockito.verify(hashCache, Mockito.times(1))
                .getHash();

        Mockito.verify(redisCacheRepository, Mockito.times(1))
                .save("abc123", "http://example.com");

        Mockito.verify(urlRepository, Mockito.times(1))
                .save("abc123", "http://example.com");

        assertEquals("http://example.com/abc123", actual);
    }

    @Test
    void getShortUrl_NoSuchElementException() {
        urlDto.setUrl("");
        assertThrows(NoSuchElementException.class, () -> urlService.getShortUrl(urlDto));
    }

    @Test
    void getOriginalUrlByCache() {
        Mockito.when(redisCacheRepository.getUrl("h6"))
                .thenReturn(urlDto.getUrl());

        String actual = urlService.getOriginalUrl("h6");
        assertEquals(urlDto.getUrl(), actual);
    }

    @Test
    void getOriginalUrlByDB() {
        Mockito.when(redisCacheRepository.getUrl("h6"))
                .thenReturn("");

        Mockito.when(urlRepository.findUrlByHash("h6"))
                .thenReturn(Optional.of(urlDto.getUrl()));

        String actual = urlService.getOriginalUrl("h6");
        assertEquals(urlDto.getUrl(), actual);
    }

    @Test
    void getOriginalUrl_NotFoundException() {
        Mockito.when(redisCacheRepository.getUrl("h6"))
                .thenReturn("");

        Mockito.when(urlRepository.findUrlByHash("h6"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrl("h6"));
    }
}