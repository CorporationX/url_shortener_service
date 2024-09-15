package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
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

    @BeforeEach
    public void setUp() {
        hash = "123456";
        url = "https://www.google.com";
        urlDto = new UrlDto(url);
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
}
