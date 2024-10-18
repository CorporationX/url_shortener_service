package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
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
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCashRepository urlCashRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private ApplicationContext context;
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

        when(hashCache.getHash()).thenReturn(hash);
        String result = urlService.getHash(url);

        verify(urlRepository, times(1)).save(associationCaptor.capture());
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
        when(context.getBean(UrlService.class)).thenReturn(urlService);
        urlService.deleteOldUrls();
        verify(context, times(1)).getBean(UrlService.class);
    }
}