package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlService urlService;

    @Test
    public void createShortLinkTest(){
        UrlDto urlDto = new UrlDto("link");
        Mockito.when(hashCache.getHash()).thenReturn("hash");
        Assertions.assertEquals("nullhash", urlService.createShortLink(urlDto));
        verify(urlCacheRepository,Mockito.times(1)).save("nullhash","link");
    }

}
