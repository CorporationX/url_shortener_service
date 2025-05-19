package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private final String testUrl = "https://example.com";
    private final String testHash = "abc123";

    @Test
    void testGetHashWhenUrlExists() {
        Url existingUrl = Url.builder().hash(testHash).url(testUrl).build();
        when(urlRepository.findByUrl(testUrl)).thenReturn(Optional.of(existingUrl));

        String result = urlService.getHash(testUrl);

        assertEquals(testHash, result);
        verify(urlRepository).findByUrl(testUrl);
        verifyNoMoreInteractions(urlRepository, hashCache, hashRepository, urlCacheRepository);
    }

    @Test
    void testGetHashWhenUrlDoesNotExist() {
        when(urlRepository.findByUrl(testUrl)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(testHash);

        String result = urlService.getHash(testUrl);

        assertEquals(testHash, result);
        verify(urlRepository).save(any(Url.class));
        verify(hashRepository).save(any(Hash.class));
        verify(urlCacheRepository).save(testHash, testUrl);
    }

    @Test
    void testGetOriginalUrlWhenFoundInCache() {
        String cachedUrl = "https://cached.com";
        when(urlCacheRepository.find(testHash)).thenReturn(cachedUrl);

        String result = urlService.getOriginalUrl(testHash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository).find(testHash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetOriginalUrlWhenFoundInDB() {
        Url url = Url.builder().hash(testHash).url(testUrl).build();
        when(urlCacheRepository.find(testHash)).thenReturn(null);
        when(urlRepository.findById(testHash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(testHash);

        assertEquals(testUrl, result);
        verify(urlCacheRepository).find(testHash);
        verify(urlRepository).findById(testHash);
    }

    @Test
    void testGetOriginalUrlWhenNotFound() {
        when(urlCacheRepository.find(testHash)).thenReturn(null);
        when(urlRepository.findById(testHash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(testHash));
        verify(urlCacheRepository).find(testHash);
        verify(urlRepository).findById(testHash);
    }
}
