package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetLongUrl_FoundInCache() {
        String hash = "abc123";
        String longUrl = "https://example.com";

        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, never()).findById(hash);
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testGetLongUrl_FoundInDatabase() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        Url urlEntity = Url.builder().hash(hash).url(longUrl).build();

        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getLongUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, times(1)).save(hash, longUrl);
    }

    @Test
    void testGetLongUrl_NotFound() {
        String hash = "abc123";

        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrl(hash));

        assertEquals("URL not found for hash: " + hash, exception.getMessage());
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testGetShortUrl() {
        String longUrl = "https://example.com";
        String hash = "abc123";

        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.getShortUrl(longUrl);

        assertEquals(hash, result);
        verify(hashCache, times(1)).getHash();
        verify(urlCacheRepository, times(1)).save(hash, longUrl);
        verify(urlRepository, times(1)).save(argThat(url ->
                url.getHash().equals(hash) && url.getUrl().equals(longUrl)));
    }
}