package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.UrlProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

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
        UrlProperties urlProperties = new UrlProperties("https://test/", 12);
        urlService = new UrlService(urlRepository, hashGeneratorService, hashCacheService,
                urlCacheRepository, urlProperties);
    }

    @Test
    void testNegativeCreateShortUrlWhenHashNotFound() {

    }

    @Test
    void testNegativeCreateShortUrlWhenSaveToCacheFailed() {

    }

    @Test
    void testPositiveCreateShortUrl() {

    }

    @Test
    void testNegativeRedirectToOriginalUrlWhenUrlNotFound() {

    }

    @Test
    void testPositiveRedirectToOriginalUrlWhenUrlCacheNotFound() {

    }

    @Test
    void testPositiveRedirectToOriginalUrlWhenUrlCacheExists() {

    }

    @Test
    void testPositiveCleanUnusedAssociations() {

    }

    private UrlDto createUrlDto(String url) {
        return UrlDto.builder()
                .longUrl(url)
                .build();
    }

    private Url createUrl(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
