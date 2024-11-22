package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.url.UrlConfig;
import faang.school.urlshortenerservice.dto.RequestUrlBody;
import faang.school.urlshortenerservice.dto.ResponseUrlBody;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hashCache.HashCache;
import faang.school.urlshortenerservice.service.urlService.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private RequestUrlBody requestUrlBody;
    private ResponseUrlBody responseUrlBody;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlConfig urlConfig;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        requestUrlBody = RequestUrlBody.builder()
                .url("http://long.url/")
                .build();

        responseUrlBody = ResponseUrlBody.builder()
                .url("http://long.url/")
                .shortUrl("http://short.url/hash123")
                .build();
    }

    @Test
    void convertLink_ShouldReturnExistingUrl() {
        Url url = Mockito.mock(Url.class);

        when(urlService.findUrlInDatabaseByFullUrl(requestUrlBody.getUrl())).thenReturn(Optional.of(url));
        when(urlConfig.getShortName()).thenReturn("http://short.url/");
        when(urlMapper.toResponseBody(url, "http://short.url/")).thenReturn(responseUrlBody);

        ResponseUrlBody response = urlService.convertLink(requestUrlBody);

        assertNotNull(response);
        assertEquals(responseUrlBody.getShortUrl(), response.getShortUrl());
    }

    @Test
    void convertLink_ShouldCreateNewUrl() {
        Url url = Mockito.mock(Url.class);

        ResponseUrlBody responseUrlBody = ResponseUrlBody.builder()
                .url("http://long.url/")
                .shortUrl("http://short.url/hashNew")
                .build();

        when(urlService.findUrlInDatabaseByFullUrl(requestUrlBody.getUrl())).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn("hashNew");
        when(urlRepository.save(any(Url.class))).thenReturn(url);
        when(urlConfig.getShortName()).thenReturn("http://short.url/");
        when(urlMapper.toResponseBody(url, "http://short.url/")).thenReturn(responseUrlBody);

        ResponseUrlBody response = urlService.convertLink(requestUrlBody);

        assertNotNull(response);
        assertEquals("http://short.url/hashNew", response.getShortUrl());
    }

    @Test
    void redirectLink_ShouldReturnFullLink() {
        Url url = Mockito.mock(Url.class);

        when(urlService.findUrlInCacheByHash("hash123")).thenReturn("http://long.url/");

        String fullLink = urlService.redirectLink("hash123");

        assertEquals("http://long.url/", fullLink);
    }

    @Test
    void redirectLink_ShouldThrowExceptionWhenUrlNotFound() {
        when(urlService.findUrlInCacheByHash("hashNotFound")).thenReturn(null);
        when(urlRepository.findByHashIgnoreCase("hashNotFound")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> urlService.redirectLink("hashNotFound"));
    }

    @Test
    void findAndReturnExpiredUrls_ShouldReturnDeletedUrls() {
        Url url1 = Mockito.mock(Url.class);
        Url url2 = Mockito.mock(Url.class);

        when(urlRepository.deleteAndReturnExpiredUrls(1)).thenReturn(List.of(url1, url2));

        List<Url> expiredUrls = urlService.findAndReturnExpiredUrls(1);

        assertEquals(2, expiredUrls.size());
    }
}
