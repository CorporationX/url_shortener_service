package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlRedis;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;


    @Test
    public void testGetLongUrlFromCache() {
        String shortUrl = "shortUrl";
        UrlRedis urlRedis = UrlRedis.builder().id(shortUrl).url("https://faang-school.com/courses").build();
        when(urlCacheRepository.findById(shortUrl)).thenReturn(Optional.of(urlRedis));

        String longUrl = urlService.getLongUrl(shortUrl);

        assertEquals("https://faang-school.com/courses", longUrl);
    }

    @Test
    public void testGetLongUrlFromDb() {
        String shortUrl = "shortUrl";
        when(urlCacheRepository.findById(shortUrl)).thenReturn(Optional.empty());

        String databaseUrl = "https://faang-school.com/courses";
        when(urlRepository.findUrlByHash(shortUrl)).thenReturn(databaseUrl);

        String longUrl = urlService.getLongUrl(shortUrl);

        assertEquals(databaseUrl, longUrl);
        verify(urlCacheRepository).save(any());
    }

    @Test
    public void testGetLongUrlNotFound() {
        String shortUrl = "shortUrl";
        when(urlCacheRepository.findById(shortUrl)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(shortUrl)).thenReturn(null);

        assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrl(shortUrl));
    }
}