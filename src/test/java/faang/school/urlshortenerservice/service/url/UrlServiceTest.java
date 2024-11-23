package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.config.url.ShortUrlProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private ShortUrlProperties shortUrlProperties;

    @Mock
    private UrlCacheRepositoryImpl urlCacheRepository;

    private static final String DOMAIN = "domain";
    private static final String HASH = "hash";
    private static final String URL = "url";
    private List<String> existingHashes;
    private Url url;
    private UrlDto longUrlDto;
    private UrlDto shortUrlDto;

    @BeforeEach
    public void init() {
        longUrlDto = UrlDto.builder()
                .url(URL)
                .build();
        url = Url.builder()
                .hash(HASH)
                .url(URL)
                .build();
        shortUrlDto = UrlDto.builder()
                .url(DOMAIN + HASH)
                .build();
        existingHashes = Arrays.asList(HASH);
        ReflectionTestUtils.setField(cacheProperties, "nonWorkingUrlTime", 1);
    }

    @Test
    @DisplayName("Success when clean DB")
    public void whenCleanDBThenDeleteOldUrlAndGetHashes() {
        when(urlRepository.getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime())).thenReturn(existingHashes);
        doNothing().when(hashRepository).saveAllHashesBatched(anyList());

        urlService.releaseExpiredHashes();

        verify(urlRepository).getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime());
        verify(hashRepository).saveAllHashesBatched(anyList());
    }

    @Test
    @DisplayName("Success when get long url by Redis cache")
    public void whenGetLongUrlByRedisCacheThenReturnLongUrl() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(HASH);

        assertNotNull(result);
        assertEquals(URL, result);
        verify(urlCacheRepository).findUrlByHash(HASH);
    }

    @Test
    @DisplayName("Success when get long url by urlRepository")
    public void whenGetLongUrlByUrlRepositoryThenReturnLongUrl() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(HASH);

        assertNotNull(result);
        assertEquals(URL, result);
        verify(urlCacheRepository).findUrlByHash(HASH);
        verify(urlRepository).findUrlByHash(HASH);
    }

    @Test
    @DisplayName("Exception when get long url")
    public void whenGetLongUrlThenThrowException() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getLongUrl(HASH));
    }

    @Test
    @DisplayName("Success when create short url")
    public void whenCreateShortUrlThenReturnUrlDto() {
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.save(url)).thenReturn(url);
        doNothing().when(urlCacheRepository).save(url);
        when(shortUrlProperties.getDomain()).thenReturn(DOMAIN);

        when(urlMapper.toEntity(longUrlDto, HASH)).thenReturn(url);
        when(urlMapper.toDto(url, DOMAIN)).thenReturn(shortUrlDto);

        UrlDto result = urlService.createShortUrl(longUrlDto);

        assertNotNull(result);
        assertEquals(DOMAIN + HASH, result.getUrl());
        verify(hashCache).getHash();
        verify(urlMapper).toEntity(longUrlDto, HASH);
        verify(urlRepository).findUrlByHash(HASH);
        verify(urlRepository).save(url);
        verify(urlCacheRepository).save(url);
        verify(shortUrlProperties).getDomain();
        verify(urlMapper).toDto(url, DOMAIN);
    }
}