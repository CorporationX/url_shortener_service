package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private LocalCache localCache;

    @Mock
    private UrlCacheRepository cacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "shortenerUrl", "http://short.ly/");
    }

    @Test
    void testGetShortUrl_ShouldReturnShortenedUrlAndSaveMapping() {
        String expectedHash = "abc123";
        when(localCache.getHash()).thenReturn(expectedHash);
        UrlDto inputDto = new UrlDto("http://example.com");

        UrlDto resultDto = urlService.getShortUrl(inputDto);

        assertNotNull(resultDto);
        assertEquals("http://short.ly/" + expectedHash, resultDto.getUrl());
        verify(cacheRepository, times(1)).save(expectedHash, "http://example.com");
    }

    @Test
    void testGetLongUrl_ShouldReturnLongUrlFromCache() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        when(cacheRepository.findByHash(hash)).thenReturn(Optional.of(originalUrl));

        String result = urlService.getLongUrl(hash);

        assertEquals(originalUrl, result);
        verify(cacheRepository, times(1)).findByHash(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetLongUrl_ShouldReturnLongUrlFromDatabaseWhenNotInCache() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        when(cacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.getByHash(hash)).thenReturn(Optional.of(originalUrl));

        String result = urlService.getLongUrl(hash);

        assertEquals(originalUrl, result);
        verify(cacheRepository, times(1)).findByHash(hash);
        verify(urlRepository, times(1)).getByHash(hash);
    }

    @Test
    void testGetLongUrl_ShouldThrowNotFoundExceptionIfUrlNotFound() {
        String hash = "abc123";
        when(cacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.getByHash(hash)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> urlService.getLongUrl(hash));
        assertEquals("The url with the " + hash + " hash cannot be found", exception.getMessage());
    }
}
