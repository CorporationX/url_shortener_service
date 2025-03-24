package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    @InjectMocks
    private UrlServiceImpl urlService;

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    private static final String HASH = "String";
    private static final String URL = "http://localhost:8090";

    @Test
    public void testGetUrlRedis() {
        when(urlCacheRepository.getByHash(HASH)).thenReturn(URL);
        String urlExpected =  urlService.getUrl(HASH);
        assertEquals(URL, urlExpected);
    }

    @Test
    public void testGetUrlNotFound() {
        when(urlCacheRepository.getByHash(HASH)).thenReturn("");
        Assert.assertThrows(
                DataNotFoundException.class,
                () -> urlService.getUrl(HASH));
    }

    @Test
    public void testGetUrlRepository() {
        Url urlEx = new Url(HASH, URL);
        when(urlCacheRepository.getByHash(HASH)).thenReturn("");
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(urlEx));
        String urlExpected =  urlService.getUrl(HASH);
        assertEquals(urlEx.getUrl(), urlExpected);
    }
}
