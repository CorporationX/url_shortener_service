package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.exception.url.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private URLCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private HashMapper hashMapper;
    @InjectMocks
    private UrlService urlService;
    private final String urlString = "https://www.google.com/search?q=amsterdam";
    private final String hashString = "C";
    private final String hashAndHost = "http://localhost:8080/shortener/C";
    private URLDto urlDto;

    @BeforeEach
    void init() {
        urlDto = URLDto.builder()
                .url(urlString)
                .build();
    }

    @Test
    @DisplayName("createShortLinkSaveInUrlRepositoryException")
    void testSaveInUrlRepositoryException() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.createShortLink(urlDto));

        assertEquals("exception", exception.getMessage());

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
    }

    @Test
    @DisplayName("createShortLinkSaveInUrlCacheRepositoryException")
    void testSaveInUrlCacheRepositoryException() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenReturn(new URL());
        doThrow(new RuntimeException("exception")).when(urlCacheRepository).save(any(URL.class));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.createShortLink(urlDto));

        assertEquals("exception", exception.getMessage());

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
        verify(urlCacheRepository, times(1)).save(any(URL.class));
    }

    @Test
    @DisplayName("createShortLinkToDtoException")
    void testCreateShortLinkToDtoException() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenReturn(new URL());
        doNothing().when(urlCacheRepository).save(any(URL.class));
        doThrow(new RuntimeException("exception")).when(hashMapper).toDto(any(Hash.class));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.createShortLink(urlDto));

        assertEquals("exception", exception.getMessage());

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
        verify(urlCacheRepository, times(1)).save(any(URL.class));
    }

    @Test
    @DisplayName("createShortLinkValid")
    void testCreateShortLinkValid() {
        when(hashCache.getHash()).thenReturn(hashString);
        when(urlRepository.save(any(URL.class))).thenReturn(new URL());
        doNothing().when(urlCacheRepository).save(any(URL.class));
        when(hashMapper.toDto(any(Hash.class))).thenReturn(new HashDto());

        urlService.createShortLink(urlDto);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(URL.class));
        verify(urlCacheRepository, times(1)).save(any(URL.class));
        verify(hashMapper, times(1)).toDto(any(Hash.class));
    }

    @Test
    @DisplayName("getUrlByHashUrlCacheRepositoryException")
    void testUrlCacheRepositoryException() {
        when(urlCacheRepository.find(anyString())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.getUrlByHash(hashAndHost));

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("getUrlByHashUrlCacheRepositoryNull")
    void testUrlCacheRepositoryNull() {
        when(urlCacheRepository.find(anyString())).thenReturn(new URL());

        String result = urlService.getUrlByHash(hashAndHost);

        assertNull(result);

        verify(urlCacheRepository, times(1)).find(anyString());
    }

    @Test
    @DisplayName("getUrlByHashUrlRepositoryException")
    void testUrlRepositoryException() {
        when(urlCacheRepository.find(anyString())).thenReturn(null);
        when(urlRepository.findUrlByHash(anyString())).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () ->
                urlService.getUrlByHash(hashAndHost));

        verify(urlCacheRepository, times(1)).find(anyString());
        verify(urlRepository, times(1)).findUrlByHash(anyString());
    }

    @Test
    @DisplayName("getUrlByHashUrlRepositoryValid")
    void testUrlRepositoryValid() {
        when(urlCacheRepository.find(anyString())).thenReturn(null);
        when(urlRepository.findUrlByHash(anyString())).thenReturn(Optional.of(urlString));

        urlService.getUrlByHash(hashAndHost);

        verify(urlCacheRepository, times(1)).find(anyString());
        verify(urlRepository, times(1)).findUrlByHash(anyString());
    }

    @Test
    @DisplayName("deleteOldURLUrlRepositoryIsEmpty")
    void testDeleteOldURLUrlRepositoryIsEmpty() {
        when(urlRepository.getHashAndDeleteURL()).thenReturn(Collections.emptyList());

        urlService.deleteOldURL();

        verify(urlRepository, times(1)).getHashAndDeleteURL();
    }

    @Test
    @DisplayName("deleteOldURLHashRepositoryException")
    void testDeleteOldURLHashRepositoryException() {
        List<String> hashes = List.of("1", "2");
        when(urlRepository.getHashAndDeleteURL()).thenReturn(hashes);
        when(hashRepository.saveAll(anyList())).thenThrow(new RuntimeException("exception"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                urlService.deleteOldURL());

        assertEquals("exception", exception.getMessage());

        verify(urlRepository, times(1)).getHashAndDeleteURL();
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("deleteOldURLValid")
    void testDeleteOldURLValid() {
        List<String> hashes = List.of("1", "2");
        when(urlRepository.getHashAndDeleteURL()).thenReturn(hashes);
        when(hashRepository.saveAll(anyList())).thenReturn(List.of(new Hash()));

        urlService.deleteOldURL();

        verify(urlRepository, times(1)).getHashAndDeleteURL();
        verify(hashRepository, times(1)).saveAll(anyList());
    }
}