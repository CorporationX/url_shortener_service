package faang.school.urlshortenerservice.manage;

import faang.school.urlshortenerservice.cache.UrlCacheManager;
import faang.school.urlshortenerservice.exception.NotFoundEntityException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled
public class UrlManagerTest {

    @Mock
    private UrlCacheManager urlCacheManager;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlManager urlManager;

    @Test
    @DisplayName("Should return URL from cache")
    public void testGetUrl_CacheHit() {
        String hash = "abc123";
        String expectedUrl = "https://example.com";

        when(urlCacheManager.get(hash)).thenReturn(expectedUrl);

        String actualUrl = urlManager.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlCacheManager).get(hash);
        verify(urlRepository, never()).findById(hash);
    }

    @Test
    @DisplayName("Should fetch URL from repository when cache miss")
    public void testGetUrl_CacheMiss() {
        String hash = "abc123";
        String expectedUrl = "https://example.com";
        Url url = new Url(hash, expectedUrl);

        when(urlCacheManager.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String actualUrl = urlManager.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlCacheManager).get(hash);
        verify(urlRepository).findById(hash);
        verify(urlCacheManager).add(url);
    }

    @Test
    @DisplayName("Should throw NotFoundEntityException when URL not found")
    public void testGetUrl_NotFound() {
        String hash = "nonexistent";

        when(urlCacheManager.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> {
            urlManager.getUrl(hash);
        });

        assertEquals("Not found Url with hash: " + hash, exception.getMessage());
        verify(urlCacheManager).get(hash);
        verify(urlRepository).findById(hash);
        verify(urlCacheManager, never()).add(any(Url.class));
    }

    @Test
    @DisplayName("Should add URL to cache")
    public void testAddCache() {
        Url url = new Url("abc123", "https://example.com");

        urlManager.addCache(url);

        verify(urlCacheManager).add(url);
    }

    @Test
    @DisplayName("Should save URL to repository")
    public void testSaveUrl() {
        String hash = "abc123";
        String urlString = "https://example.com";
        Url url = new Url(hash, urlString);

        when(urlRepository.save(any(Url.class))).thenReturn(url);

        Url savedUrl = urlManager.saveUrl(hash, urlString);

        assertEquals(url, savedUrl);
        verify(urlRepository).save(new Url(hash, urlString));
    }

    @Test
    @DisplayName("Should remove expired hashes and update cache")
    public void testGetExpiredHashesAndDelete() {
        LocalDateTime dateExpired = LocalDateTime.now().minusDays(1);
        List<String> expiredHashes = List.of("hash1", "hash2");

        when(urlRepository.removeExpiredHash(dateExpired)).thenReturn(expiredHashes);

        List<String> result = urlManager.getExpiredHashesAndDelete(dateExpired);

        assertEquals(expiredHashes, result);
        verify(urlCacheManager).remove(expiredHashes);
    }

    @Test
    @DisplayName("Should return empty list when no expired hashes")
    public void testGetExpiredHashesAndDelete_EmptyList() {
        LocalDateTime dateExpired = LocalDateTime.now().minusDays(1);
        List<String> expiredHashes = List.of();

        when(urlRepository.removeExpiredHash(dateExpired)).thenReturn(expiredHashes);

        List<String> result = urlManager.getExpiredHashesAndDelete(dateExpired);

        assertTrue(result.isEmpty());
        verify(urlCacheManager, never()).remove(anyList());
    }
}