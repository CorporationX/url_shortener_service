package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cach.LocalCache;
import faang.school.urlshortenerservice.entity.AssociationHashUrl;
import faang.school.urlshortenerservice.entity.UrlCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private LocalCache localCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCashRepository urlCashRepository;
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private UrlService urlService;
    private String url;
    private String hash;

    @BeforeEach
    void setUp() {
        url = "https://www.google.com";
        hash = "hash";
    }

    @Test
    void testGetHashFromRedis() {
        ReflectionTestUtils.setField(urlService, "staticUrl", "http://sh.c/");
        UrlCache urlCacheFromRedis = new UrlCache(hash, url);
        when(urlCashRepository.findByUrl(url)).thenReturn(urlCacheFromRedis);
        String result = urlService.getHash(url);
        assertEquals("http://sh.c/" + hash, result);
    }

    @Test
    void testGetHash() {
        ReflectionTestUtils.setField(urlService, "staticUrl", "http://sh.c/");
        ArgumentCaptor<UrlCache> urlCaptor = ArgumentCaptor.forClass(UrlCache.class);
        ArgumentCaptor<AssociationHashUrl> associationCaptor = ArgumentCaptor.forClass(AssociationHashUrl.class);

        when(localCache.getHash()).thenReturn(hash);
        String result = urlService.getHash(url);

        verify(urlRepository,times(1)).save(associationCaptor.capture());
        verify(urlCashRepository, times(1)).save(urlCaptor.capture());
        assertEquals("http://sh.c/" + hash, result);
        assertEquals(hash, urlCaptor.getValue().getHash());
        assertEquals(url, urlCaptor.getValue().getUrl());
    }

    @Test
    void testGetLongUrlFromRedis() {
        UrlCache urlCache = new UrlCache(hash, "https://www.google.com");
        when(urlCashRepository.findById(hash)).thenReturn(Optional.of(urlCache));
        String result = urlService.getLongUrl(hash);
        assertEquals("https://www.google.com", result);
    }

    @Test
    void testGetLongUrl() {
        AssociationHashUrl associationHashUrl = AssociationHashUrl.builder().hash(hash).url(url).build();
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(associationHashUrl));
        String result = urlService.getLongUrl(hash);
        assertEquals(url, result);
    }

    @Test
    void testGetLongUrlNotFound() {
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrl(hash));
    }

    @Test
    void testDeleteOldUrls() {
        when(urlRepository.existsRecordsOlderThanOneYear()).thenReturn(true);
        urlService.deleteOldUrls();
        verify(urlRepository, times(1)).deleteAndReturnOldUrls();
        verify(hashRepository, times(1)).save(anyList());
    }
}