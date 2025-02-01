package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createShortUrlValidHashSuccessTest() {
        String url = "https://example.com";
        String hash = "abc123";

        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrlAndSave(url);

        assertEquals(hash, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(new Url(hash, url));
    }

    @Test
    void createShortUrlNullHashFailTest() {
        String url = "https://example.com";

        when(hashCache.getHash()).thenReturn(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> urlService.createShortUrlAndSave(url));
        assertEquals("Failed to generate a hash for the URL.", exception.getMessage());

        verify(hashCache).getHash();
        verifyNoInteractions(urlRepository);
    }


    @Test
    void getUrlByHashSuccessTest() {
        String hash = "abc123";
        String url = "https://example.com";
        Url urlEntity = new Url(hash, url);

        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getUrlByHash(hash);

        assertEquals(url, result);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void getUrlByHashFailTest() {
        String hash = "abc123";

        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getUrlByHash(hash));
        assertEquals("URL not found for hash: abc123", exception.getMessage());

        verify(urlRepository).findByHash(hash);
    }
}
