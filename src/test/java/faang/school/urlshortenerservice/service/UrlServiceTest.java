package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.NoHashAvailableException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.interfaces.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private static final String ORIGINAL_URL = "https://example.com";
    private static final String HASH = "abc123";
    private static final String SHORT_URL = "http://short.url/" + HASH;
    private static final UrlDto URL_ENTITY = new UrlDto(HASH, ORIGINAL_URL, LocalDateTime.now());

    @Test
    void shortenUrlSuccess() {
        when(urlCacheRepository.findHashByUrl(ORIGINAL_URL)).thenReturn(null);
        when(urlRepository.findByUrl(ORIGINAL_URL)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        String result = urlService.shortenUrl(ORIGINAL_URL);

        assertEquals(SHORT_URL, result);

        verify(urlCacheRepository).findHashByUrl(ORIGINAL_URL);
        verify(urlRepository).findByUrl(ORIGINAL_URL);
        verify(hashCache).getHash();
        verify(urlRepository).findByHash(HASH);
        verify(urlRepository).save(HASH, ORIGINAL_URL);
        verify(urlCacheRepository).save(HASH, ORIGINAL_URL);
        verifyNoMoreInteractions(hashCache, urlRepository, urlCacheRepository);
    }

    @Test
    void testShortenUrlHashCacheEmptyThrowsException() {
        when(hashCache.getHash()).thenThrow(new NoHashAvailableException("No hashes available"));

        NoHashAvailableException exception = assertThrows(NoHashAvailableException.class, this::execute);
        assertEquals("No hashes available", exception.getMessage());

        verify(urlRepository, never()).save(HASH, ORIGINAL_URL);
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testShortenUrlHashCollisionThrowsException() {
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(URL_ENTITY));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.shortenUrl(ORIGINAL_URL));
        assertEquals("Hash already exists", exception.getMessage());

        verify(urlRepository, never()).save(HASH, ORIGINAL_URL);
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testShortenUrlExistingUrlInCache() {
        when(urlCacheRepository.findHashByUrl(ORIGINAL_URL)).thenReturn(HASH);

        String result = urlService.shortenUrl(ORIGINAL_URL);

        assertEquals(SHORT_URL, result);
        verify(urlRepository, never()).findByUrl(anyString());
        verify(urlRepository, never()).save(SHORT_URL, ORIGINAL_URL);
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testShortenUrlExistingUrlInDb() {
        when(urlCacheRepository.findHashByUrl(ORIGINAL_URL)).thenReturn(null);
        when(urlRepository.findByUrl(ORIGINAL_URL)).thenReturn(Optional.of(URL_ENTITY));

        String result = urlService.shortenUrl(ORIGINAL_URL);

        assertEquals(SHORT_URL, result);
        verify(urlCacheRepository).save(HASH, ORIGINAL_URL);
        verify(urlRepository, never()).save(HASH, ORIGINAL_URL);
    }

    @Test
    void testGetOriginalUrlSuccessFromCache() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(ORIGINAL_URL);

        String result = urlService.getOriginalUrl(HASH);

        assertEquals(ORIGINAL_URL, result);
        verify(urlRepository, never()).findByHash(anyString());
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void testGetOriginalUrlSuccessFromDb() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(URL_ENTITY));

        String result = urlService.getOriginalUrl(HASH);

        assertEquals(ORIGINAL_URL, result);
        verify(urlCacheRepository).save(HASH, ORIGINAL_URL);
    }

    @Test
    void testGetOriginalUrlNotFoundThrowsException() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, this::execute2);
        assertEquals("URL not found for hash: " + HASH, exception.getMessage());

        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    private void execute() {
        urlService.shortenUrl(ORIGINAL_URL);
    }

    private void execute2() {
        urlService.getOriginalUrl(HASH);
    }
}