package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCache;
import faang.school.urlshortenerservice.cashe.ShortUrlCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortUrlCache shortUrlCache;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private String existingHash;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "domain", "http://test.com");
        existingHash = "existingHash";
    }

    @Test
    void TestCreateShortUrl_returnExistingIfExists() {
        String longUrl = "http://example.com";
        when(urlRepository.hashForUrlIfExists(longUrl)).thenReturn(existingHash);
        assertEquals("http://test.com/existingHash", urlService.createShortUrl(longUrl));
        verify(urlRepository, never()).save(any());
        verify(shortUrlCache, never()).saveUrl(any(), any());
    }

    @Test
    void TestCreateShortUrl_newIfNotExists() {
        String longUrl = "http://example.com/new";
        String newHash = "newHash";
        when(urlRepository.hashForUrlIfExists(longUrl)).thenReturn(null);
        when(hashCache.getHash()).thenReturn(newHash);

        assertEquals("http://test.com/newHash", urlService.createShortUrl(longUrl));
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(shortUrlCache, times(1)).saveUrl(newHash, longUrl);
    }

    @Test
    void TestCreateShortUrl_handleConstraintViolation() {
        String longUrl = "http://example.com/duplicate";
        when(urlRepository.hashForUrlIfExists(longUrl)).thenReturn(null, existingHash);
        when(hashCache.getHash()).thenReturn("newHash");
        when(urlRepository.save(any(Url.class))).thenThrow(new ConstraintViolationException("Duplicate key", null));

        assertEquals("http://test.com/existingHash", urlService.createShortUrl(longUrl));
    }

    @Test
    void TestGetOriginalUrl_returnCachedUrlIfPresent() {
        String hash = "cachedHash";
        String cachedUrl = "http://cached.com";
        when(shortUrlCache.getUrl(hash)).thenReturn(cachedUrl);

        assertEquals(cachedUrl, urlService.getOriginalUrl(hash));
        verify(urlRepository, never()).findById(anyString());
    }

    @Test
    void TestGetOriginalUrl_fromRepoIfNotCached() {
        String hash = "repositoryHash";
        String repoUrl = "http://repository.com";
        when(shortUrlCache.getUrl(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(new Url
                (hash, repoUrl, Timestamp.valueOf(LocalDateTime.now()))));

        assertEquals(repoUrl, urlService.getOriginalUrl(hash));
    }

    @Test
    void TestGetOriginalUrl_throwExceptionIfUrlNotFound() {
        String hash = "notFoundHash";
        when(shortUrlCache.getUrl(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }
}