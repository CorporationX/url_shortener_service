package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.Assert;
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
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @InjectMocks
    private UrlService urlService;

    private String hash;
    private String url;
    private UrlDto urlDto;
    private Url longUrl;

    @BeforeEach
    public void setUp() {
        hash = "123456";
        url = "https://www.google.com";
        urlDto = new UrlDto(url);
        longUrl = Url.builder().hash(hash).url(url).build();
    }

    @Test
    public void shortenUrlTest() {
        Mockito.when(urlRepository.save(Mockito.any(Url.class))).thenAnswer(i -> i.getArgument(0));
        Mockito.doNothing().when(urlCacheRepository).save(Mockito.anyString(), Mockito.anyString());
        Mockito.when(hashCache.getHash()).thenReturn(hash);
        urlService.shortenUrl(urlDto);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).save(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(urlRepository, Mockito.times(1)).save(Mockito.any(Url.class));
    }

    @Test
    public void getLongUrlFromCacheTest() {
        Mockito.when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(url));
        String actualUrl = urlService.getLongUrl(hash);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).findByHash(hash);
        Mockito.verify(urlRepository, Mockito.times(0)).findByHash(hash);
        Assert.assertEquals(actualUrl, longUrl.getUrl());
    }

    @Test
    public void getLongUrlNotFromCacheTest() {
        Mockito.when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        Mockito.when(urlRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));
        String actualLongUrl = urlService.getLongUrl(hash);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).findByHash(hash);
        Mockito.verify(urlRepository, Mockito.times(1)).findByHash(hash);
        Assert.assertEquals(actualLongUrl, longUrl.getUrl());
    }

    @Test
    public void getLongUrlWithExceptionTest() {
        Mockito.when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        Mockito.when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrl(hash));
    }
}
