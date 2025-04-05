package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.generator.RedisCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private RedisCache redisCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private final String hash = "tst";
    private final String url = "testurl";

   @Test
    public void testGetUrlByHashSuccess() {
        Mockito.when(redisCache.getUrlFromCache(hash)).thenReturn(url);
        String result = urlService.getUrlByHash(hash);

        assertEquals(url, result);
    }

    @Test
    public void testGetUrlByHashUrlNotFound() {
       Mockito.when(redisCache.getUrlFromCache(hash)).thenThrow(UrlNotFoundException.class);

       Assert.assertThrows(UrlNotFoundException.class, () -> {redisCache.getUrlFromCache(hash);});
    }

    @Test
    public void testGetHashByUrlSuccess() {
        Mockito.when(hashCache.getHash()).thenReturn(hash);
        String result = urlService.getHashByUrl(url);

        assertEquals(hash, result);
    }

    @Test
    public void testGetHashByUrlFailedNotFound() {
        Mockito.when(hashCache.getHashes()).thenThrow(UrlNotFoundException.class);

        Assert.assertThrows(UrlNotFoundException.class, () -> {hashCache.getHashes();});
    }
}