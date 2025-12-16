package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import faang.school.urlshortenerservice.dto.CreateUrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest
{
    private final static String URL_PREFIX = "127.0.0.1:8080";
    private final static String URL = "https://google.com";
    private final static String HASH = "85nd";

    private final String hashValue = HASH;
    private final String urlValue = URL;
    private final String urlPrefix = URL_PREFIX;
    private final String shortUrl = String.format("%s/%s", URL_PREFIX, HASH);

    List<String> hashes = List.of("1fg", "0or");
    CreateUrlRequestDto createUrlRequestDto = CreateUrlRequestDto
            .builder()
            .url(urlValue)
            .build();

    Url url = Url.builder()
            .hash(hashValue)
            .url(urlValue)
            .build();

    ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlShortenerConfig urlShortenerConfig;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;


    @Test
    void testSuccessfullyOneYearOldUrlDeleted() {
        when(urlRepository.deleteOldUrlHashes()).thenReturn(hashes);
        urlShortenerService.deleteOneYearOldUrl();
        verify(hashRepository, times(1)).save(hashes);
        verify(urlRepository, times(1)).deleteOldUrlHashes();
    }

    @Test
    void testSuccessfullyOneYearOldUrlDeletedWhenListIsEmpty() {
        when(urlRepository.deleteOldUrlHashes()).thenReturn(Collections.emptyList());
        urlShortenerService.deleteOneYearOldUrl();
        verify(urlRepository, times(1)).deleteOldUrlHashes();
        verify(hashRepository, never()).save(anyList());
    }

    @Test
    void testSuccessfullyShortUrlCreated() {
        when(hashCache.getHash()).thenReturn(Optional.of(hashValue));
        when(urlShortenerConfig.getUrlPrefix()).thenReturn(urlPrefix);
        String result = urlShortenerService.createShortUrl(createUrlRequestDto);
        assertEquals(shortUrl, result);
        verify(urlRepository, times(1)).save(urlCaptor.capture());
        Url saved = urlCaptor.getValue();
        assertEquals(hashValue, saved.getHash());
        assertEquals(urlValue, saved.getUrl());
        verify(urlCacheRepository, times(1)).save(hashValue, urlValue);
    }

    @Test
    void testFaileShortUrlCreatedWhenNoHashAvailable() {
        when(hashCache.getHash()).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> urlShortenerService.createShortUrl(createUrlRequestDto)
        );
        assertEquals("No free hashes available", ex.getMessage());
    }

    @Test
    void testSuccessfullyOriginalUrlGot() {
        when(urlCacheRepository.get(hashValue)).thenReturn(urlValue);
        String result = urlShortenerService.getOriginalUrl(hashValue);
        assertEquals(urlValue, result);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void getOriginalUrl_shouldReadFromDbAndCacheResult_whenNotCached() {
        when(urlCacheRepository.get(hashValue)).thenReturn(null);
        when(urlRepository.findByHash(hashValue)).thenReturn(url);
        String result = urlShortenerService.getOriginalUrl(hashValue);
        assertEquals(urlValue, result);
        verify(urlCacheRepository, times(1)).save(hashValue, urlValue);
    }

    @Test
    void getOriginalUrl_shouldThrowUrlNotFound_whenDbThrows() {
        when(urlCacheRepository.get(hashValue)).thenReturn(null);
        when(urlRepository.findByHash(hashValue))
                .thenThrow(new RuntimeException("Url not found"));
        UrlNotFoundException ex = assertThrows(
                UrlNotFoundException.class,
                () -> urlShortenerService.getOriginalUrl(hashValue)
        );
        assertEquals("Url not found by hash " + hashValue, ex.getMessage());
    }
}
