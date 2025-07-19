package faang.school.urlshortenerservice.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.cache.HashCache;
import faang.school.urlshortenerservice.util.cache.UrlRedisCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRedisCache redisCache;
    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService service;

    private static final String hash = "34affw34t23Afdsf";
    private static final String url = "https://www.pirojok.com/java";

    @Test
    public void createTest() {

        UrlDto testDto = new UrlDto(url);
        when(hashCache.getHash()).thenReturn(new Hash(hash));

        assertNotNull(service.create(testDto));
        verify(redisCache, times(1)).save(hash, url);
        verify(urlRepository, times(1)).save(Url.builder().hash(hash).url(url).build());
    }

    @Test
    public void findRedisTest() {
        when(redisCache.get(hash)).thenReturn(url);
        assertEquals(url, service.find(hash));
    }

    @Test
    public void findRepoTest() {
        when(redisCache.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(Url.builder().hash(hash).url(url).build()));
        assertEquals(url, service.find(hash));
    }

    @Test
    public void couldNotFindTest() {
        when(redisCache.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.find(hash));
    }
}
