package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.properties.UrlProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    private final String url = "https://www.google.com";
    private final String hash = "hash1";
    private final UrlProperties urlProperties = new UrlProperties("https://test/", 12);
    private final Url entity = createUrl();

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashGeneratorService hashGeneratorService;

    @Mock
    private HashCacheService hashCacheService;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository, hashGeneratorService, hashCacheService,
                urlCacheRepository, urlProperties);
    }

    @Test
    void testNegativeCreateShortUrlWhenHashNotFound() {
        when(hashCacheService.getHash()).thenReturn(null);
        assertThrows(HashNotFoundException.class, () -> urlService.createShortUrl(createUrlDto()));
    }

    @Test
    void testPositiveCreateShortUrl() {
        when(hashCacheService.getHash()).thenReturn(hash);
        when(urlRepository.save(any())).thenReturn(entity);
        when(urlCacheRepository.cacheUrl(entity)).thenReturn(entity);

        String result = urlService.createShortUrl(createUrlDto());

        assertEquals(result, urlProperties.pattern().concat(hash));
    }

    @Test
    void testNegativeRedirectToOriginalUrlWhenUrlNotFound() {
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(null);
        when(urlRepository.getByHash(hash)).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> urlService.redirectToOriginalUrl(hash));
    }

    @Test
    void testPositiveRedirectToOriginalUrlWhenUrlCacheNotFound() {
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(null);
        when(urlRepository.getByHash(hash)).thenReturn(Optional.of(entity));

        String result = urlService.redirectToOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).cacheUrl(entity);
        assertEquals(result, url);
    }

    @Test
    void testPositiveRedirectToOriginalUrlWhenUrlCacheExists() {
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(entity);

        String result = urlService.redirectToOriginalUrl(hash);

        verify(urlCacheRepository, times(0)).cacheUrl(entity);
        assertEquals(result, url);
    }

    @Test
    void testPositiveCleanUnusedAssociations() {
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4");
        when(urlRepository.findAndDeleteByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(hashes);

        urlService.cleanUnusedAssociations();

        verify(urlCacheRepository, times(hashes.size())).evictUrlByHash(any(String.class));
        verify(hashGeneratorService, times(1)).processAllHashes(hashes);
    }

    private UrlDto createUrlDto() {
        return UrlDto.builder()
                .longUrl(url)
                .build();
    }

    private Url createUrl() {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
