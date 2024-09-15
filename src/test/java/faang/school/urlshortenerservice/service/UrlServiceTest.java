package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    UrlRepository urlRepository;

    @Mock
    UrlCacheRepository urlCacheRepository;

    @Mock
    HashCache hashCache;

    @InjectMocks
    UrlService urlService;

    String hash;
    String urlString;
    Url url;
    UrlDto urlDto;

    @BeforeEach
    void setUp() {
        hash = "hash";
        urlString = "url";
        url = new Url();
        url.setUrl(urlString);
        url.setHash(hash);
        urlDto = new UrlDto();
        urlDto.setUrl(urlString);
    }

    @Test
    @DisplayName("Should return URL when found in cache")
    void getLongUrlByHash_foundInCache() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(urlString));

        String result = urlService.getLongUrlByHash(hash);

        verify(urlCacheRepository).findByHash(hash);
        assertNotNull(result);
        assertEquals(urlString, result);
    }

    @Test
    @DisplayName("Should return URL when found in database and cache is empty")
    void getLongUrlByHash_FoundInDatabase() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrlByHash(hash);

        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findByHash(hash);
        assertNotNull(result);
        assertEquals(urlString, result);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when URL is not found in cache or database")
    void getLongUrlByHash_NotFound() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrlByHash(hash));
    }

    @Test
    @DisplayName("Should create short URL and return hash")
    void createShortUrl() {
        when(hashCache.getHash()).thenReturn(CompletableFuture.completedFuture(hash));

        String result = urlService.createShortUrl(urlDto);

        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(hash, urlString);
        assertNotNull(result);
        assertEquals(hash, result);
    }
}