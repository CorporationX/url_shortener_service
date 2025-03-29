package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.Dto.UrlDto;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.DomainConfig;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private DomainConfig domainConfig;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private final String testHash = "abc123";
    private final String testUrl = "https://example.com";

    @Test
    void testCreateShortLink() {
        UrlDto urlDto = new UrlDto(testUrl);
        String baseUrl = "http://localhost:8080";
        when(domainConfig.getBaseUrl()).thenReturn(baseUrl);
        when(hashCache.getHash()).thenReturn(testHash);

        String result = urlService.createShortLink(urlDto);

        Assertions.assertEquals(String.format("%s/%s", baseUrl, testHash), result);
        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(testHash, testUrl);
    }

    @Test
    void testGetOriginalUrl_Success() {
        Url url = new Url(testHash, testUrl);
        when(urlRepository.findById(testHash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(testHash);

        Assertions.assertEquals(testUrl, result);
        verify(urlRepository).findById(testHash);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        when(urlRepository.findById(testHash)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(testHash));
    }
}