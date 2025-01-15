package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {
    @Mock
    private RedisTemplate<String, URL> redisTemplate;

    @Mock
    private ValueOperations<String, URL> valueOperations;

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    @Test
    public void testSaveAtRedis() throws Exception {
        String hash = "abc123";
        URL url = new URL("http://original.url");
        Url urlObject = new Url();
        urlObject.setHash(hash);
        urlObject.setUrl(url);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        urlCacheRepository.saveAtRedis(urlObject);

        verify(redisTemplate.opsForValue(), times(1)).set(hash, url, 1, TimeUnit.DAYS);
    }

    @Test
    public void testGetFromRedis() throws MalformedURLException {
        String hash = "abc123";
        URL expectedUrl = new URL("http://original.url");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(expectedUrl);

        URL actualUrl = urlCacheRepository.getFromRedis(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(redisTemplate.opsForValue(), times(1)).get(hash);
    }

    @Test
    public void testDeleteFromRedis() {
        String hash = "abc123";

        urlCacheRepository.deleteFormRedis(hash);

        verify(redisTemplate, times(1)).delete(hash);
    }
}