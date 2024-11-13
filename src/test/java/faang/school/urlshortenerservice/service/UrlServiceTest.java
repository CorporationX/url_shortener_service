package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.GeneralProperties;
import faang.school.urlshortenerservice.config.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.entity.UrlBuilder;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.postgres.url.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.validator.AppUrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    private static final String LONG_URL = "https://faang-school.com/courses";
    private static final String HASH = "qwerty";
    private static final String APP_URL = "http://localhot:8080";
    private static final String GET_URL_PATH = "/url/";

    @Mock
    private AppUrlValidator appUrlValidator;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private GeneralProperties generalProperties;

    @InjectMocks
    private UrlService urlService;

    @Test
    void testGenerateShortUrl_Success() {
        doNothing().when(appUrlValidator).validate(LONG_URL);
        when(hashCache.getHash()).thenReturn(HASH);
        when(generalProperties.getAppUrl()).thenReturn(APP_URL);

        String result = urlService.generateShortUrl(LONG_URL);

        assertEquals(APP_URL + GET_URL_PATH + HASH, result);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(anyString(), anyString());
    }

    @Test
    void testGetUrlByHash_UrlFoundInCache() {
        when(urlCacheRepository.get(HASH)).thenReturn(LONG_URL);

        String result = urlService.getUrlByHash(HASH);

        assertEquals(LONG_URL, result);
        verify(urlCacheRepository).get(HASH);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void testGetUrlByHash_UrlNotInCacheButFoundInDb() {
        when(urlCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(UrlBuilder.build(HASH, LONG_URL)));

        String result = urlService.getUrlByHash(HASH);

        assertEquals(LONG_URL, result);
        verify(urlCacheRepository).get(HASH);
        verify(urlRepository).findByHash(HASH);
    }

    @Test
    void testGetUrlByHash_UrlNotFoundInCacheOrDb() {
        when(urlCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> urlService.getUrlByHash(HASH));

        verify(urlCacheRepository).get(HASH);
        verify(urlRepository).findByHash(HASH);
    }

    @Test
    void testCleanHashes_Success() {
        when(urlRepository.deleteAndReturnOldUrls(any(LocalDateTime.class))).thenReturn(List.of(HASH));

        List<String> hashes = urlService.cleanHashes();

        assertEquals(1, hashes.size());
        assertEquals(HASH, hashes.get(0));
    }
}