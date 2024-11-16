package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

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
    private RedisTemplate redisTemplate;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private static final String URL = "url";
    private static final String HASH = "hash";
    private Url url;
    private UrlDto urlDto;
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
        urlDto = UrlDto.builder()
                .url(HASH)
                .build();
        shortUrlDto = UrlDto.builder()
                .url(HASH)
                .build();
    }

    @Test
    @DisplayName("Success when create short url")
    public void whenCreateShortUrlThenReturnUrlDto() {
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlMapper.toEntity(longUrlDto, HASH)).thenReturn(url);
        when(urlRepository.save(url)).thenReturn(url);
        when(urlCacheRepository.save(url)).thenReturn(url);
        when(urlMapper.toDto(url)).thenReturn(shortUrlDto);

        UrlDto result = urlService.createShortUrl(longUrlDto);

        assertNotNull(result);
        assertEquals(HASH, result.getUrl());
        verify(hashCache).getHash();
        verify(urlMapper).toEntity(longUrlDto, HASH);
        verify(urlRepository).save(url);
        verify(urlCacheRepository).save(url);
        verify(urlMapper).toDto(url);
    }
}