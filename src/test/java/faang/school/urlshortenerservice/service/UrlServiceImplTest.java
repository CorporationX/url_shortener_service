package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.cache.UrlRedisCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlRedisCache urlRedisCache;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void testCreateShortUrl() {
        UrlDto urlDto = new UrlDto("https://example.com");
        when(hashCache.getHash()).thenReturn("abc123");
        doNothing().when(urlRepository).save(urlDto.url(), "abc123");
        doNothing().when(urlRedisCache).save(urlDto.url(), "abc123");

        UrlDto result = urlService.createShortUrl(urlDto, "domain");

        assertNotNull(result);
        assertEquals("domainabc123", result.url());
        verify(hashCache).getHash();
        verify(urlRepository).save(urlDto.url(), "abc123");
        verify(urlRedisCache).save(urlDto.url(), "abc123");
    }

}