package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashService hashService;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private final String url = "https://example.com";
    private final String hash = "abc123";
    private final Url urlEntity = Url.builder()
            .hash(hash)
            .url(url)
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    void shorten_shouldSaveAndReturnHash() {
        when(hashService.getNextHash()).thenReturn(hash);

        String result = urlService.shorten(url);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).put(eq(hash), eq(url));
        assertEquals(hash, result);
    }

    @Test
    void resolve_shouldReturnCachedUrlWhenPresent() {
        when(urlCacheRepository.get(hash)).thenReturn(url);

        String result = urlService.resolve(hash);

        assertEquals(url, result);
    }

    @Test
    void resolve_shouldFetchFromDbWhenNotCached() {
        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.resolve(hash);

        assertEquals(url, result);
    }

    @Test
    void resolve_shouldThrowWhenNotFound() {
        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> urlService.resolve(hash));
    }
}
