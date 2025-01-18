package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.UrlUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlUtil urlUtil;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private faang.school.urlshortenerservice.service.UrlService urlService;

    @Test
    void createShortUrlInvalidOriginalTest() {
        String invalidUrl = "http://example.com/invalid^";
        UrlDto urlDto = new UrlDto(invalidUrl);
        when(urlUtil.ensureUrlHasProtocol(invalidUrl)).thenReturn(invalidUrl);
        when(urlUtil.isValidUrl(any(String.class))).thenReturn(false);

        assertThrows(DataValidationException.class, () -> urlService.createShortUrl(urlDto));

        verify(urlUtil, times(1)).ensureUrlHasProtocol(invalidUrl);
        verify(urlUtil, times(1)).isValidUrl(any(String.class));
    }

    @Test
    void createShortUrlValidTest() {
        String validUrl = "http://example.com/valid";
        UrlDto urlDto = new UrlDto(validUrl);
        String hash = "21j";
        String expectedShortUrl = "localhost:8080/api/v1/url/%s".formatted(hash);
        when(urlUtil.ensureUrlHasProtocol(validUrl)).thenReturn(validUrl);
        when(urlUtil.isValidUrl(validUrl)).thenReturn(true);
        when(urlUtil.buildShortUrlFromContext(anyString())).thenReturn(expectedShortUrl);
        when(hashCache.getHash()).thenReturn(hash);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);

        String shortUrl = urlService.createShortUrl(urlDto);

        verify(urlUtil, times(1)).ensureUrlHasProtocol(validUrl);
        verify(urlUtil, times(1)).isValidUrl(validUrl);
        verify(urlUtil, times(1)).buildShortUrlFromContext(hash);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(urlCaptor.capture());
        verify(urlCacheRepository, times(1)).saveDefaultUrl(hash, validUrl);

        assertEquals(expectedShortUrl, shortUrl);
        assertEquals(validUrl, urlCaptor.getValue().getUrl());
        assertEquals(hash, urlCaptor.getValue().getHash());
    }

    @Test
    void getOriginalUrlNotFoundTest() {
        String hash = "214kj";
        when(urlRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        verify(urlRepository, times(1)).findOriginalUrlByHash(hash);
    }

    @Test
    void getOriginalUrlDbFoundTest() {
        String originalUrl = "youtube.com";
        String hash = "214kj";
        when(urlRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.of(originalUrl));

        String resultUrl = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, resultUrl);
        verify(urlRepository, times(1)).findOriginalUrlByHash(hash);
    }

    @Test
    void findUrlEntitiesValidTest() {
        List<Url> urlEntitiesFromDb = List.of(new Url());
        when(urlRepository.findByHashes(any())).thenReturn(urlEntitiesFromDb);

        List<Url> urlEntities = urlService.findUrlEntities(new HashSet<>());
        assertEquals(urlEntitiesFromDb, urlEntities);
    }
}