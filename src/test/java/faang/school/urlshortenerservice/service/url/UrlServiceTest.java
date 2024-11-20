package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.config.properties.redis.RedisProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    private static final String VALID_URL = "https://www.google.com";
    private static final String HASH_FROM_CACHE = "xCv";

    @InjectMocks
    private UrlService urlService;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private RedisProperties redisProperties;
    private LongUrlDto longUrlDto;

    @BeforeEach
    void setUp() {
        longUrlDto = LongUrlDto.builder()
                .url(VALID_URL)
                .build();
    }

    @Test
    @DisplayName("When valid dto passed then save it in redis, db, and return hash from cache")
    public void whenValidDtoPassedThenSaveItAndReturnHash() {
        when(hashCache.getOneHash()).thenReturn(HASH_FROM_CACHE);
        String hashResult = urlService.saveAndConvertLongUrl(longUrlDto);

        verify(urlRepository).save(any());
        verify(urlCacheRepository).save(HASH_FROM_CACHE, longUrlDto.getUrl(), redisProperties.getTtl());

        assertEquals(hashResult, HASH_FROM_CACHE);
    }
}
