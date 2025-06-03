package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.ExceptionHandler.Errors;
import faang.school.urlshortenerservice.ExceptionHandler.UrlNotFoundException;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Captor
    ArgumentCaptor<Url> urlCaptor;

    @Test
    void positiveCreateShortUrlShouldGenerateHash() {
        String hash = "abc123";
        String originalUrl = "https://example.com";

        Mockito.when(hashCache.getHash()).thenReturn(hash);

        UrlResponse response = urlService.createShortUrl(originalUrl);

        assertEquals(hash, response.shortUrl());

        Mockito.verify(hashCache).getHash();
        Mockito.verify(urlRepository).save(urlCaptor.capture());
        Url savedUrl = urlCaptor.getValue();
        assertEquals(hash, savedUrl.getHash());
        assertEquals(originalUrl, savedUrl.getUrl());

        Mockito.verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    void positiveGetOriginalUrlShouldReturnFromCacheIfPresent() {
        String hash = "abc123";
        String originalUrl = "https://cached.com";

        Mockito.when(urlCacheRepository.findOriginalUrl(hash))
                .thenReturn(Optional.of(originalUrl));
        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);

        Mockito.verify(urlCacheRepository).findOriginalUrl(hash);
        Mockito.verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void positiveGetOriginalUrlShouldReturnFromDbAndCacheIfNotInCache() {
        String hash = "abc123";
        String originalUrl = "https://fromdb.com";
        Url urlFromDb = Url.builder().hash(hash).url(originalUrl).build();

        Mockito.when(urlCacheRepository.findOriginalUrl(hash))
                .thenReturn(Optional.empty());
        Mockito.when(urlRepository.findByHash(hash))
                .thenReturn(Optional.of(urlFromDb));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);

        Mockito.verify(urlCacheRepository).findOriginalUrl(hash);
        Mockito.verify(urlRepository).findByHash(hash);
        Mockito.verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    void negativeGetOriginalUrlShouldThrowExceptionIfNotFoundAnywhere() {
        String hash = "notfound";

        Mockito.when(urlCacheRepository.findOriginalUrl(hash))
                .thenReturn(Optional.empty());
        Mockito.when(urlRepository.findByHash(hash))
                .thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getOriginalUrl(hash)
        );

        assertEquals("with hash :" + hash, exception.getMessage());
        assertEquals(Errors.NOT_FOUND, exception.getError());

        Mockito.verify(urlCacheRepository).findOriginalUrl(hash);
        Mockito.verify(urlRepository).findByHash(hash);
    }
}
