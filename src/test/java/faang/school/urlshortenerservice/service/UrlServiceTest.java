package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCash;
import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCash hashCash;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    private Url url;
    private UrlDtoRequest urlDtoRequest;
    private String hash;

    private String urlPrefix;

    @BeforeEach
    void init() {
        hash = "Sd5";
        String urlString = "http://example.com";
        urlDtoRequest = UrlDtoRequest.builder()
                .url(urlString)
                .build();
        url = Url.builder()
                .url(urlString)
                .hash(hash)
                .build();
        urlPrefix = "http://exampele.com/";
        urlService.setUrlPrefix(urlPrefix);
    }

    @Test
    void getShortUrlTest() {
        String shortUrl = urlPrefix + hash;
        when(hashCash.getHash()).thenReturn(hash);
        String actual = urlService.getShortUrl(urlDtoRequest);
        verify(urlRepository).save(any());
        verify(urlCacheRepository).saveUrlByHash(hash, urlDtoRequest.getUrl());
        assertNotNull(actual);
        assertEquals(shortUrl, actual);
    }

    @Test
    void getUrlFormHashFromCashTest(){
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.of(url.getUrl()));
        String actual = urlService.getUrlFromHash(hash);
        assertEquals(url.getUrl(), actual);
        verify(urlCacheRepository).getUrlByHash(hash);

    }
    @Test
    void getUrlFromHashFromRepositoryTest(){
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));
        String actual = urlService.getUrlFromHash(hash);
        assertEquals(url.getUrl(), actual);
        verify(urlCacheRepository).getUrlByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    void getUrlFromRepositoryNotFoundTest(){
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> urlService.getUrlFromHash(hash));
    }
}
