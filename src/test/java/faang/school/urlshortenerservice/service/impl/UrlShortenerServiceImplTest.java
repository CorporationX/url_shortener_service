package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.entity.RedisCachedUrl;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    private static final String LONG_URL = "https://example.com/long-url";
    private static final String HASH_STRING = "abc123";
    private static final String SHORT_URL = "https://urlshrinker.com/" + HASH_STRING;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(urlShortenerService, "domainName", "https://urlshrinker.com");
    }

    @Test
    @DisplayName("Should return existing shortened URL if long URL already exists in repository")
    public void textShrinkUrl_ReturnExisting() {
        Url existingUrl = new Url();
        existingUrl.setHash(HASH_STRING);
        existingUrl.setLongUrl(LONG_URL);

        when(urlRepository.findByLongUrl(LONG_URL)).thenReturn(Optional.of(existingUrl));

        String shortenedUrl = urlShortenerService.shrinkUrl(existingUrl);

        assertEquals(SHORT_URL, shortenedUrl);
        verify(urlCacheRepository, times(1)).save(any(RedisCachedUrl.class));
    }

    @Test
    @DisplayName("Should create and return new shortened URL if long URL does not exist in repository")
    public void textShrinkUrl_CreateNew() {
        Url newUrlEntity = new Url();
        newUrlEntity.setLongUrl(LONG_URL);

        when(urlRepository.findByLongUrl(LONG_URL)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(HASH_STRING);

        String shortenedUrl = urlShortenerService.shrinkUrl(newUrlEntity);

        assertEquals(SHORT_URL, shortenedUrl);
        verify(urlCacheRepository, times(1)).save(any(RedisCachedUrl.class));
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Should return original URL from cache if it exists")
    public void testGetOriginalUrl_ReturnFromCache() {
        RedisCachedUrl cachedUrl = new RedisCachedUrl();
        cachedUrl.setId(HASH_STRING);
        cachedUrl.setLongUrl(LONG_URL);

        when(urlCacheRepository.findById(HASH_STRING)).thenReturn(Optional.of(cachedUrl));

        String originalUrl = urlShortenerService.getOriginalUrl(HASH_STRING);

        assertEquals(LONG_URL, originalUrl);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    @DisplayName("Should return original URL from database if not in cache")
    public void testGetOriginalUrl_ReturnFromDatabase() {
        Url urlEntity = new Url();
        urlEntity.setHash(HASH_STRING);
        urlEntity.setLongUrl(LONG_URL);

        when(urlRepository.findByHash(HASH_STRING)).thenReturn(Optional.of(urlEntity));
        when(urlCacheRepository.findById(HASH_STRING)).thenReturn(Optional.empty());

        String originalUrl = urlShortenerService.getOriginalUrl(HASH_STRING);

        assertEquals(LONG_URL, originalUrl);
        verify(urlRepository, times(1)).findByHash(HASH_STRING);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if shortened URL does not exist in cache or database")
    public void testGetOriginalUrl_ThrowsException() {

        when(urlCacheRepository.findById(HASH_STRING)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(HASH_STRING)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                urlShortenerService.getOriginalUrl(HASH_STRING)
        );
        assertEquals("The specified shortened URL does not exist. Please create a new shortened URL.", exception.getMessage());
    }
}
