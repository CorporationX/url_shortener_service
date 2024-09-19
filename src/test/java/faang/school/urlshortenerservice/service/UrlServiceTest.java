package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    private UrlService urlService;
    private int ttlInMinutes;

    private Hash hash;
    UrlDto urlDto;
    private Url url;

    @BeforeEach
    void setUp() {
        ttlInMinutes = 5;
        urlService = new UrlService(urlRepository, urlCacheRepository, hashCache, ttlInMinutes);
        hash = new Hash("test");
        urlDto = new UrlDto("https://anytesturl.com");
        url = new Url(hash.getHash(), urlDto.getUrl());
    }

    @Test
    void testCreateUrl() {
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(url)).thenReturn(url);

        String expected = urlService.createUrl(urlDto);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(url);
        verify(urlCacheRepository, times(1)).save(url, ttlInMinutes);
        assertEquals(expected, url.getHash());
    }

    @Test
    void testGetUrlWhenCacheExists() {
        when(urlCacheRepository.getByHash(hash.getHash())).thenReturn(Optional.of(url));

        String actual = urlService.getUrl(hash.getHash());
        verify(urlCacheRepository, times(1)).getByHash(hash.getHash());
        verify(urlRepository, times(0)).findById(any());
        verify(urlCacheRepository, times(0)).save(any(Url.class), anyInt());
        assertEquals(url.getUrl(), actual);
    }

    @Test
    void testGetUrlWhenCacheNotExists() {
        when(urlCacheRepository.getByHash(hash.getHash())).thenReturn(Optional.empty());
        when(urlRepository.findById(hash.getHash())).thenReturn(Optional.of(url));

        String actual = urlService.getUrl(hash.getHash());
        verify(urlCacheRepository, times(1)).getByHash(hash.getHash());
        verify(urlRepository, times(1)).findById(hash.getHash());
        verify(urlCacheRepository, times(1)).save(url, ttlInMinutes);
        assertEquals(url.getUrl(), actual);
    }

    @Test
    void testGetUrlWhenCacheAndDBNotExists() {
        when(urlCacheRepository.getByHash(hash.getHash())).thenReturn(Optional.empty());
        when(urlRepository.findById(hash.getHash())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(hash.getHash()));
        verify(urlCacheRepository, times(1)).getByHash(hash.getHash());
        verify(urlRepository, times(1)).findById(hash.getHash());
        verify(urlCacheRepository, times(0)).save(any(Url.class), anyInt());
    }
}