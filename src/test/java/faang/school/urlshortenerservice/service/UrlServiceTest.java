package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @InjectMocks
    private UrlService urlService;
    private final String urlString = "https://www.google.com/search?q=amsterdam";
    private final String hashString = "C";
    private final String hashAndHost = "http://localhost:8080/shortener/C";
    private final String removedPeriod = "1 year";
    private UrlDto urlDto;

    @BeforeEach
    void init() {
        urlDto = UrlDto.builder()
                .url(urlString)
                .build();
    }

    @Test
    @DisplayName("createShortLinkSaveInUrlRepositoryException")
    void testSaveInUrlRepositoryException() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.create(urlDto));

        assertEquals("exception", exception.getMessage());

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
    }

    @Test
    @DisplayName("createShortLinkSaveInUrlCacheRepositoryException")
    void testSaveInUrlCacheRepositoryException() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenReturn(new URL());
        doThrow(new RuntimeException("exception")).when(urlCacheRepository).save(anyString(), anyString());

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.create(urlDto));

        assertEquals("exception", exception.getMessage());

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
        verify(urlCacheRepository, times(1)).save(anyString(), anyString());
    }

    @Test
    @DisplayName("createShortLinkValid")
    void testCreateShortLinkValid() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenReturn(new URL());
        doNothing().when(urlCacheRepository).save(anyString(), anyString());

        urlService.create(urlDto);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
        verify(urlCacheRepository, times(1)).save(anyString(), anyString());
    }

    @Test
    @DisplayName("getUrlByHashUrlCacheRepositoryException")
    void testUrlCacheRepositoryException() {
        when(urlCacheRepository.findUrlByHash(anyString())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.getUrlByHash(hashAndHost));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("getUrlByHashUrlRepositoryException")
    void testUrlRepositoryException() {
        when(urlCacheRepository.findUrlByHash(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(anyString())).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () ->
                urlService.getUrlByHash(hashAndHost));

        verify(urlCacheRepository, times(1)).findUrlByHash(anyString());
        verify(urlRepository, times(1)).findUrlByHash(anyString());
    }

    @Test
    @DisplayName("getUrlByHashUrlRepositoryValid")
    void testUrlRepositoryValid() {
        when(urlCacheRepository.findUrlByHash(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(anyString())).thenReturn(Optional.of(urlString));

        urlService.getUrlByHash(hashAndHost);

        verify(urlCacheRepository, times(1)).findUrlByHash(anyString());
        verify(urlRepository, times(1)).findUrlByHash(anyString());
    }

    @Test
    @DisplayName("deleteOldURLUrlRepositoryIsEmpty")
    void testDeleteOldURLUrlRepositoryIsEmpty() {
        when(urlRepository.getHashAndDeleteURL(anyString())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> urlService.deleteOldURL(removedPeriod));

        verify(urlRepository, times(1)).getHashAndDeleteURL(anyString());
    }

    @Test
    @DisplayName("deleteOldURLHashRepositoryException")
    void testDeleteOldURLHashRepositoryException() {
        List<String> hashes = List.of("1", "2");
        when(urlRepository.getHashAndDeleteURL(anyString())).thenReturn(Optional.of(hashes));
        when(hashRepository.saveAll(anyList())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.deleteOldURL(removedPeriod));

        assertEquals("exception", exception.getMessage());

        verify(urlRepository, times(1)).getHashAndDeleteURL(anyString());
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("deleteOldURLValid")
    void testDeleteOldURLValid() {
        List<String> hashes = List.of("1", "2");
        when(urlRepository.getHashAndDeleteURL(anyString())).thenReturn(Optional.of(hashes));
        when(hashRepository.saveAll(anyList())).thenReturn(List.of(new Hash()));

        urlService.deleteOldURL(removedPeriod);

        verify(urlRepository, times(1)).getHashAndDeleteURL(anyString());
        verify(hashRepository, times(1)).saveAll(anyList());
    }
}