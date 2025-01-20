package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private faang.school.urlshortenerservice.service.UrlService urlService;

    @Test
    void createShortUrlValidTest() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "localhost:8080/api/v1/urls");
        UrlDto urlDto = new UrlDto("http://example.com");
        String hash = "21j";
        String expectedShortUrl = "localhost:8080/api/v1/urls/%s".formatted(hash);
        when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.createShortUrl(urlDto);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any());
        assertEquals(expectedShortUrl, shortUrl);
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
    void deleteOldUrlsTest() {
        assertDoesNotThrow(() -> urlService.deleteOldUrls());

        verify(urlRepository, times(1)).deleteOldUrls(any());
        verify(hashRepository, times(1)).save(any());
    }
}