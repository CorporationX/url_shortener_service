package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @InjectMocks
    private UrlServiceImpl urlService;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Captor
    private ArgumentCaptor<Url> urlCaptor;

    private Hash hash;
    private Url url;

    @BeforeEach
    void setUp() {
        hash = Hash.builder().hash("hash").build();
        url = Url.builder().url("url").hash("hash").createdAt(LocalDateTime.now()).build();
    }

    @Test
    void testGenerateShortUrlAndSaveItToRepository() {
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        urlService.getShortenUrl(UrlDto.builder().url("url").build());

        verify(urlRepository, times(1)).save(urlCaptor.capture());
        verify(hashCache, times(1)).getHash();
        assertEquals(urlCaptor.getValue().getUrl(), url.getUrl());
    }


    @Test
    void testGetOriginalUrlFoundInCache() {
        when(urlCacheRepository.get(hash.getHash())).thenReturn(Optional.of(url.getHash()));
        String result = urlService.getOriginalUrl(hash.getHash());

        assertEquals(url.getHash(), result);
        verify(urlCacheRepository, times(1)).get(hash.getHash());
        verify(urlRepository, never()).findByHash(hash.getHash());
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testGetOriginalUrlNotFoundInCacheButFoundInDb() {
        when(urlCacheRepository.get(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(anyString())).thenReturn(Optional.of(new Url(hash.getHash(), url.getUrl(), LocalDateTime.now())));
        String result = urlService.getOriginalUrl(hash.getHash());

        assertEquals(url.getUrl(), result);
        verify(urlCacheRepository, times(1)).get(hash.getHash());
        verify(urlRepository, times(1)).findByHash(hash.getHash());
        verify(urlCacheRepository, times(1)).save(hash.getHash(), url.getUrl());
    }

    @Test
    void testGetOriginalUrlNotFoundInCacheAndDb() {
        when(urlCacheRepository.get(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(anyString())).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash.getHash()));

        verify(urlCacheRepository, times(1)).get(hash.getHash());
        verify(urlRepository, times(1)).findByHash(hash.getHash());
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

}