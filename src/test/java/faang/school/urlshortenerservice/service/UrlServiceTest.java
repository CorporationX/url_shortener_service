package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.сache.HashCache;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashService hashService;

    private Hash hash;
    private Url url;
    private UrlDto urlDto;

    @BeforeEach
    void setUp() {
        hash = new Hash("1");
        url = Url.builder()
                .hash(hash.getHash())
                .url("url")
                .build();
        urlDto =  UrlDto.builder()
                .url(url.getUrl())
                .build();
    }

    @Test
    void testSaveUrlExists() {
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.existsById(hash.getHash())).thenReturn(true);

        urlService.save(urlDto);

        verify(urlRepository, times(0)).save(any(Url.class));
        verify(urlCacheRepository, times(0)).saveUrl(url);
    }

    @Test
    void testSaveUrlNotExists() {
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.existsById(hash.getHash())).thenReturn(false);

        urlService.save(urlDto);

        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).saveUrl(url);
    }
    @Test
    void testGetUrlFromCache() {
        when(urlCacheRepository.getUrl(hash.getHash())).thenReturn(url.getUrl());

        String foundUrl = urlService.getUrl(hash.getHash());

        assertEquals(url.getUrl(), foundUrl);
        verify(urlRepository, times(0)).findById(anyString());
    }

    @Test
    void testGetUrlFromDbWhenCacheMiss() {
        when(urlCacheRepository.getUrl(hash.getHash())).thenReturn(null);
        when(urlRepository.findById(hash.getHash())).thenReturn(Optional.of(url));

        String foundUrl = urlService.getUrl(hash.getHash());

        assertEquals(url.getUrl(), foundUrl);
        verify(urlRepository, times(1)).findById(hash.getHash());
    }

    @Test
    void testGetUrlThrowsExceptionWhenNotFound() {
        when(urlCacheRepository.getUrl(hash.getHash())).thenReturn(null);
        when(urlRepository.findById(hash.getHash())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(hash.getHash()));

        assertEquals(String.format("Url with hash: %s not found", hash.getHash()), exception.getMessage());
        verify(urlRepository, times(1)).findById(hash.getHash());
    }

    @Test
    void testDeleteOlderUrls() {
        List<Hash> hashes = Collections.singletonList(new Hash("1"));
        when(urlRepository.deleteOlderOneYearUrls()).thenReturn(hashes);

        urlService.deleteOlderUrls();

        verify(urlRepository, times(1)).deleteOlderOneYearUrls();
        verify(hashService, times(1)).saveBatch(hashes);
    }
}
