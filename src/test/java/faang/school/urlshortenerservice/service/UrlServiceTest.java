package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;

    private String shortenerUrl;
    private String resultUrl;
    private String hash;
    private Url url;
    private UrlDto urlDto;

    @BeforeEach
    public void init(){
        hash = "test";
        shortenerUrl = "testUrl";
        String longUrl = "testForUrlService";
        resultUrl = shortenerUrl + hash;

        urlDto = UrlDto.builder()
                .url(longUrl)
                .build();

        url = Url.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();
    }

    @Test
    public void testGetShortUrlWithGetting(){
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(url)).thenReturn(null);
        doNothing().when(urlCacheRepository).save(hash, urlDto.getUrl());
        ReflectionTestUtils.setField(urlService, "shortenerUrl", shortenerUrl);

        UrlDto result = urlService.getShortUrl(urlDto);

        InOrder inOrder = Mockito.inOrder(hashCache, urlRepository, urlCacheRepository);
        inOrder.verify(hashCache, times(1)).getHash();
        inOrder.verify(urlRepository, times(1)).save(url);
        inOrder.verify(urlCacheRepository, times(1)).save(hash, urlDto.getUrl());
        inOrder.verifyNoMoreInteractions();

        assertEquals(resultUrl, result.getUrl());
    }

    @Test
    public void testGetLongUrlWithGettingFromCash(){
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(resultUrl));

        String result = urlService.getLongUrl(hash);

        verify(urlCacheRepository, times(1)).findByHash(hash);
        assertEquals(resultUrl, result);
    }

    @Test
    public void testGetLongUrlWithGettingFromDB(){
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.getByHash(hash)).thenReturn(Optional.of(resultUrl));

        String result = urlService.getLongUrl(hash);

        InOrder inOrder = Mockito.inOrder(urlCacheRepository, urlRepository);
        inOrder.verify(urlCacheRepository, times(1)).findByHash(hash);
        inOrder.verify(urlRepository, times(1)).getByHash(hash);
        inOrder.verifyNoMoreInteractions();
        assertEquals(resultUrl, result);
    }

    @Test
    public void testGetLongUrlWithNotFoundException(){
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.getByHash(hash)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, ()->urlService.getLongUrl(hash));

        InOrder inOrder = Mockito.inOrder(urlCacheRepository, urlRepository);
        inOrder.verify(urlCacheRepository, times(1)).findByHash(hash);
        inOrder.verify(urlRepository, times(1)).getByHash(hash);
        inOrder.verifyNoMoreInteractions();
        assertEquals("The url with the " + hash + " hash cannot be found", exception.getMessage());
    }
}
