package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.url.UrlException;
import faang.school.urlshortenerservice.service.hash.HashCache;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.url.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    private static final String URL_NOT_FOUND_MESSAGE = "Url by this hash not found not found";
    private static final String INVALID_URL_MESSAGE = "Url %s is not a valid url";

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private HashCache hashCache;

    @Captor
    private ArgumentCaptor<Url> urlArgumentCaptor;

    @InjectMocks
    private UrlService urlService;

    @Test
    void testGetOriginalUrlFromDb() {
        String hash = "hash";
        String url = "url";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(Url.builder().hash(hash).url(url).build()));

        String actualUrl = urlService.getOriginalUrl(hash);
        assertEquals(url, actualUrl);
        verify(urlCacheRepository).save(hash, url);
    }

    @Test
    void testGetOriginalUrlFromCache() {
        String hash = "hash";
        String url = "url";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(url));

        String actualUrl = urlService.getOriginalUrl(hash);
        assertEquals(url, actualUrl);
    }

    @Test
    void testGetOriginalUrlNotFound() {
        String hash = "hash";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals(URL_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    void testCreateShortSuccess() {
        String hash = "hash";
        String url = "url";
        when(urlValidator.isValidUrl(url)).thenReturn(true);
        when(hashCache.getFreeHash()).thenReturn(hash);
        Url hashedUrl = Url.builder()
                .hash(hash)
                .url(url)
                .build();

        urlService.createShort(UrlDto.builder().url(url).build());

        verify(urlRepository).save(urlArgumentCaptor.capture());
        verify(urlCacheRepository).save(hash, url);
        Url actual = urlArgumentCaptor.getValue();
        assertEquals(hashedUrl.getUrl(), actual.getUrl());
        assertEquals(hashedUrl.getHash(), actual.getHash());
    }

    @Test
    void testCreateShortWithInvalidUrl() {
        String url = "url";
        when(urlValidator.isValidUrl(url)).thenReturn(false);

        UrlException exception
                = assertThrows(UrlException.class, () -> urlService.createShort(UrlDto.builder().url(url).build()));

        assertEquals(String.format(INVALID_URL_MESSAGE, url), exception.getMessage());
    }
}