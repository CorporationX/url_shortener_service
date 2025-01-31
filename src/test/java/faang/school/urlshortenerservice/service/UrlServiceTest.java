package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Test
    void testFindUrl_WhenCacheMiss() {
        String hash = "By4";
        String expectedUrl = "https://example.com";

        Url entity = Url.builder().hash(hash).url(expectedUrl).build();

        when(urlCacheRepository.findUrlFromCache(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(entity));

        String result = urlService.findUrl(hash);

        assertEquals(expectedUrl, result);

        verify(urlCacheRepository).findUrlFromCache(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    void testSaveNewHash() {
        UrlDto urlDto = UrlDto.builder().url("https://example.com").build();

        Hash hash = Hash.builder().hash("hash1").build();
        Url url = Url.builder().hash("hash1").url("https://example.com").build();

        when(hashCache.getHash()).thenReturn(hash);

        urlService.saveNewHash(urlDto);

        verify(urlRepository).save(url);
        verify(urlCacheRepository).saveToCache(url);
    }

    @Test
    void testFindUrl_ThrowsUrlNotFoundException() {
        String hash = "invalidHash";

        when(urlCacheRepository.findUrlFromCache(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, () -> urlService.findUrl(hash));

        assertEquals(String.format("URL with hash %s not found", hash), exception.getMessage());

        verify(urlCacheRepository).findUrlFromCache(hash);
        verify(urlRepository).findById(hash);
    }
}
