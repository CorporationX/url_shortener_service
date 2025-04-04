package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UrlServiceImplTest {

    @Autowired
    private UrlServiceImpl urlService;

    @MockBean
    private UrlCacheRepository urlCacheRepository;

    @MockBean
    private UrlRepository urlRepository;

    @MockBean
    private HashCache hashCache;

    @MockBean
    private UrlMapper urlMapper;

    private final String testUrl = "http://example.com";
    private final String testHash = "abc123";
    private final String baseUrl = "http://localhost:8080/url/";

    @Test
    void testGetShortUrlSuccess() {

        UrlDto urlDto = new UrlDto(testUrl);
        when(hashCache.getHashFromCache()).thenReturn(testHash);
        when(urlMapper.toEntity(any())).thenReturn(new Url(testHash, testUrl, LocalDateTime.now()));

        String shortUrl = urlService.getShortUrl(urlDto);

        assertEquals(baseUrl + testHash, shortUrl);
        verify(urlCacheRepository).save(testHash, testUrl);
        verify(urlRepository).save(any());
    }

    @Test
    void testRedirectToRealUrlFromCache() {

        when(urlCacheRepository.findByHashInRedis(testHash)).thenReturn(testUrl);

        String result = urlService.redirectToRealUrl(testHash);

        assertEquals(testUrl, result);
        verify(urlCacheRepository).findByHashInRedis(testHash);
        verify(urlRepository, never()).findByHash(any());
    }

    @Test
    void testRedirectToRealUrlFromDatabase() {

        Url urlEntity = new Url(testHash, testUrl, LocalDateTime.now());
        when(urlCacheRepository.findByHashInRedis(testHash)).thenReturn(null);
        when(urlRepository.findByHash(testHash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.redirectToRealUrl(testHash);

        assertEquals(testUrl, result);
        verify(urlCacheRepository).findByHashInRedis(testHash);
        verify(urlRepository).findByHash(testHash);
    }

    @Test
    void testRedirectToRealUrlNotFound() {

        when(urlCacheRepository.findByHashInRedis(testHash)).thenReturn(null);
        when(urlRepository.findByHash(testHash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> {
            urlService.redirectToRealUrl(testHash);
        });

        verify(urlCacheRepository).findByHashInRedis(testHash);
        verify(urlRepository).findByHash(testHash);
    }
}