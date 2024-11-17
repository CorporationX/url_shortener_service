package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.Link;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @InjectMocks
    private UrlService urlService;

    @Test
    public void createShortLinkTest() {
        Link link = new Link("linkComing");
        Mockito.when(hashCache.getHash()).thenReturn("anyhash");
        Assertions.assertEquals("anyhash", urlService.createShortUrl(link));
        verify(urlCacheRepository, Mockito.times(1)).save("anyhash", "linkComing");
    }
}