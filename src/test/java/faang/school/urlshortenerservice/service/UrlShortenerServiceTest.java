package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlsDto;
import faang.school.urlshortenerservice.mapper.UrlsDtoMapper;
import faang.school.urlshortenerservice.model.Urls;
import faang.school.urlshortenerservice.repository.RedisRepository;
import faang.school.urlshortenerservice.repository.UrlsRepository;
import faang.school.urlshortenerservice.repository.interfaces.UrlsJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {
    @Mock
    private UrlsDtoMapper urlsDtoMapper;
    @Mock
    private UrlsRepository urlsRepository;
    @Mock
    private UrlsJpaRepository urlsJpaRepository;
    @Mock
    private RedisRepository redisRepository;
    @Mock
    private LocalCache localCache;
    @InjectMocks
    private UrlShortenerService urlShortenerService;

    private String urlName;
    String hash;
    String longUrl;

    @BeforeEach
    void setUp() {
        urlName = "http://test-shortner-service.com/";
        longUrl = "http://www.test-urlshortener.com/long-url/v1/there-is-a-long-url-here";
        hash = "a1b1";
        ReflectionTestUtils.setField(urlShortenerService, "urlName", urlName);
    }

    @Test
    void getShortUrlSuccessTest() {
        UrlsDto urlsDto = UrlsDto.builder().url(longUrl).hash(hash).build();
        UrlDto expectedUrlDto = new UrlDto(urlName + hash);
        Urls urls = Urls.builder().url(longUrl).hash(hash).build();

        when(localCache.getHash()).thenReturn(hash);
        when(urlsRepository.getUrlsJpaRepository()).thenReturn(urlsJpaRepository);
        when(urlsJpaRepository.save(urls)).thenReturn(urls);
        when(urlsDtoMapper.toUrls(urlsDto)).thenReturn(urls);
        doNothing().when(redisRepository).setUrl(hash, longUrl);

        UrlDto urlDtoResult = urlShortenerService.getShortUrl(longUrl);

        verify(localCache).getHash();
        verify(urlsRepository).getUrlsJpaRepository();
        verify(redisRepository).setUrl(hash, longUrl);

        assertEquals(expectedUrlDto, urlDtoResult, "The short url is not as expected");
    }

    @Test
    void getLongUrlFromRedisSuccessTest() {
        UrlDto urlDtoExpected = UrlDto.builder().url(longUrl).build();

        when(redisRepository.getUrl(hash)).thenReturn(longUrl);

        UrlDto urlDtoResult = urlShortenerService.getLongUrl(hash);

        verify(redisRepository).getUrl(hash);
        verify(urlsRepository, times(0)).findByHash(any());
        assertEquals(urlDtoExpected, urlDtoResult, "The long url is not as expected");
    }

    @Test
    void getLongUrlFromDBSuccessTest() {
        String notFoundInRedis = null;
        Urls urls = Urls.builder().url(longUrl).hash(hash).build();
        UrlDto urlDtoExpected = UrlDto.builder().url(longUrl).build();

        when(redisRepository.getUrl(hash)).thenReturn(notFoundInRedis);
        when(urlsRepository.findByHash(hash)).thenReturn(urls);
        when(urlsDtoMapper.toUrlDtoLongUrl(urls)).thenReturn(urlDtoExpected);
        doNothing().when(redisRepository).setUrl(hash, longUrl);

        UrlDto urlDtoResult = urlShortenerService.getLongUrl(hash);

        verify(redisRepository, times(1)).getUrl(any());
        verify(urlsRepository, times(1)).findByHash(any());
        verify(redisRepository, times(1)).setUrl(any(), any());
        assertEquals(urlDtoExpected, urlDtoResult, "The long url is not as expected");
    }
}