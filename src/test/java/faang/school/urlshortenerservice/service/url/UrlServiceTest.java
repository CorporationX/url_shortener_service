package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheService urlCacheService;

    @Mock
    private HashCacheService hashCacheService;

    private static final String baseUrl = "http://short.url/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://short.url/");
    }

    @Test
    void testGetUrlByHash_fromCache() {
        String hash = "abc123";
        Url cachedUrl = new Url(hash, "https://example.com", null);

        when(urlCacheService.getUrl(hash)).thenReturn(cachedUrl);

        String result = urlService.getUrlByHash(hash);

        assertEquals("https://example.com", result);
        verify(urlRepository, never()).findByHash(any());
    }

    @Test
    void testGetUrlByHash_fromDatabase() {
        String hash = "abc123";
        Url dbUrl = new Url(hash, "https://example.com", null);

        when(urlCacheService.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(dbUrl);

        String result = urlService.getUrlByHash(hash);

        assertEquals("https://example.com", result);
        verify(urlCacheService).saveUrl(hash, dbUrl);
    }

    @Test
    void testGetUrlByHash_notFound() {
        String hash = "notfound";

        when(urlCacheService.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(null);

        String result = urlService.getUrlByHash(hash);

        assertTrue(result.contains("not found"));
    }

    @Test
    void testCreateShortUrl_success() {
        UrlDto dto = new UrlDto();
        dto.setUrl("https://example.com");
        String hash = "abc123";

        when(hashCacheService.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(dto);

        assertEquals(baseUrl + "/" + hash, result);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(urlCaptor.capture());
        assertEquals("https://example.com", urlCaptor.getValue().getUrl());
        assertEquals("abc123", urlCaptor.getValue().getHash());

        verify(urlCacheService).saveUrl(eq(hash), any(Url.class));
    }
}