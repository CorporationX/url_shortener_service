package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlService urlService;
    private final String hostName = "shrtr.com/";
    private final String bigUrl = "https://google.com/aaaaaaaaaaaaaaaaaaaa";
    private final Hash hash = new Hash("hash");
    private final Url urlEntity = Url.builder().url(bigUrl).hash(hash.getHash()).build();

    @BeforeEach
    public void init() {
        urlService.setHostName(hostName);
    }

    @Test
    public void testAdd() {
        String expected = hostName + hash.getHash();

        Mockito.when(hashCache.getHash()).thenReturn(hash);
        String result = urlService.add(bigUrl);

        Assertions.assertEquals(expected, result);
        Mockito.verify(urlRepository, Mockito.times(1)).save(urlEntity);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).put(hash.getHash(), bigUrl);
    }

    @Test
    public void testGetInRedis() {
        Mockito.when(urlCacheRepository.get(hash.getHash())).thenReturn(bigUrl);

        String result = urlService.get(hash.getHash());

        Assertions.assertEquals(bigUrl, result);
        Mockito.verify(urlRepository, Mockito.times(0)).findByHash(Mockito.anyString());
    }

    @Test
    public void testGetInBase() {
        Mockito.when(urlRepository.findByHash(hash.getHash())).thenReturn(Optional.of(urlEntity));

        String result = urlService.get(hash.getHash());

        Assertions.assertEquals(bigUrl, result);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).get(hash.getHash());
        Mockito.verify(urlRepository, Mockito.times(1)).findByHash(hash.getHash());
    }

    @Test
    public void testGetError() {
        String exceptionMessage = "URL on " + hostName + hash.getHash() + " not found!";
        Assertions.assertThrows(EntityNotFoundException.class, () -> urlService.get(hash.getHash()), exceptionMessage);

        Mockito.verify(urlCacheRepository, Mockito.times(1)).get(hash.getHash());
        Mockito.verify(urlRepository, Mockito.times(1)).findByHash(hash.getHash());
    }
}
