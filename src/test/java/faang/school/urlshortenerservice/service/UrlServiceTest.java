package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.hesh.HashCache;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    private static final String URL_PATH = "http://localhost:8085/url/";
    private static final String LONG_URL = "https://example.com/resource";
    private static final String HASH = "abc123";

    @Mock
    private HashCache hashCoach;

    @Mock
    private HashJpaRepository hashRepository;

    @Mock
    private UrlJpaRepository urlRepository;

    @Mock
    private RedisCacheRepository redisCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "urlPath", URL_PATH);
    }

    @Test
    void testCreateShortUrl() {
        UrlDto urlDto = new UrlDto(LONG_URL);
        when(hashCoach.getHash()).thenReturn(HASH);
        doNothing().when(redisCacheRepository).save(HASH, LONG_URL);

        Url urlEntity = new Url(HASH, LONG_URL, LocalDateTime.now());
        when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);

        String shortUrl = urlService.createShortUrl(urlDto);

        assertNotNull(shortUrl);
        assertEquals(URL_PATH + HASH, shortUrl);
        verify(hashCoach).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(redisCacheRepository).save(HASH, LONG_URL);
    }

    @Test
    void testGetOriginalUrl_FromRedis() {
        when(redisCacheRepository.get(HASH)).thenReturn(LONG_URL);

        String originalUrl = urlService.getOriginalUrl(HASH);

        assertNotNull(originalUrl);
        assertEquals(LONG_URL, originalUrl);
        verify(redisCacheRepository).get(HASH);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void testGetOriginalUrl_FromDatabase() {
        when(redisCacheRepository.get(HASH)).thenReturn(null);

        Url urlEntity = new Url(HASH, LONG_URL, LocalDateTime.now());
        when(urlRepository.findByHash(HASH)).thenReturn(urlEntity);
        doNothing().when(redisCacheRepository).save(HASH, LONG_URL);

        String originalUrl = urlService.getOriginalUrl(HASH);

        assertNotNull(originalUrl);
        assertEquals(LONG_URL, originalUrl);
        verify(urlRepository).findByHash(HASH);
        verify(redisCacheRepository).save(HASH, LONG_URL);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        when(redisCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(null);

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
            urlService.getOriginalUrl(HASH);
        });

        assertNotNull(exception);
        assertEquals("Url with hash abc123 was not found in database", exception.getMessage());
        verify(urlRepository).findByHash(HASH);
        verify(redisCacheRepository, never()).save(anyString(), anyString());
    }

}
