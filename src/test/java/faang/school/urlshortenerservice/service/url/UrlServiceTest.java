package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.search.SearchesService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private SearchesService searchesService1;

    @Mock
    private SearchesService searchesService2;

    private List<SearchesService> urlServices;
    private String url;
    private String hash;

    @BeforeEach
    public void setUp() {
        urlServices = Arrays.asList(searchesService1, searchesService2);
        urlService = new UrlService(urlRepository, hashCache, urlServices);
        url = "http://example.com";
        hash = "abc123";
    }

    @Test
    public void testTransformUrlToHashWithCorrectWork() {
        when(hashCache.getRandomHashFromCache()).thenReturn(hash);

        String result = urlService.transformUrlToHash(url);

        verify(urlRepository, times(1)).save(any(Url.class));
        verify(hashCache, times(1)).saveToCache(hash, url);
        assertEquals(hash, result);
    }

    @Test
    public void testGetUrlFromHashWhenHashExists() {
        when(searchesService1.findUrl(hash)).thenReturn(Optional.empty());
        when(searchesService2.findUrl(hash)).thenReturn(Optional.of(url));

        String result = urlService.getUrlFromHash(hash);

        assertEquals(url, result);
        verify(searchesService1, times(1)).findUrl(hash);
        verify(searchesService2, times(1)).findUrl(hash);
    }

    @Test
    public void testGetUrlFromHashWhenThereIsHash() {
        when(searchesService1.findUrl(hash)).thenReturn(Optional.of(url));

        String result = urlService.getUrlFromHash(hash);

        assertEquals(url, result);
        verify(searchesService1, times(1)).findUrl(hash);
        verify(searchesService2, never()).findUrl(hash);
    }

    @Test
    public void testGetUrlFromHashWhenHashDoesNotExist() {
        when(searchesService1.findUrl(hash)).thenReturn(Optional.empty());
        when(searchesService2.findUrl(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            urlService.getUrlFromHash(hash);
        });

        assertEquals("URL not found for hash: " + hash, exception.getMessage());
    }
}