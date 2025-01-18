package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.UrlShortenerApplicationTests;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlServiceIT extends UrlShortenerApplicationTests {

    @Autowired
    private UrlService urlService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UrlRepositoryImpl urlRepository;

    private String hash;
    private String originalUrl;
    private String domain;
    private UrlDto urlDto = new UrlDto();

    @BeforeEach
    public void setUp() {
        hash = "hash";
        originalUrl = "https://github.com/CorporationX/url_shortener_service";
        domain = "http://localhost:8080/api/v1/urls/";
        urlDto.setUrl(originalUrl);

        String deleteFromUrlQuery = "DELETE FROM url";
        jdbcTemplate.update(deleteFromUrlQuery);

        String deleteFromHashQuery = "DELETE FROM hash";
        jdbcTemplate.update(deleteFromHashQuery);
    }

    @Test
    public void shortenUrlTest() {
        String shortenedUrl = urlService.shortenUrl(urlDto);
        String hash = shortenedUrl.replace(domain, "");

        String savedOriginalUrl = urlRepository.findOriginalUrlByHash(hash).get();

        assertEquals(urlDto.getUrl(), savedOriginalUrl);
    }

    @Test
    public void getOriginalUrlContainsInCacheTest() {
        String shortenedUrl = urlService.shortenUrl(urlDto);
        String hashFromMethod = shortenedUrl.replace(domain, "");

        String returnedUrl = urlService.getOriginalUrl(hashFromMethod);

        Cache result = cacheManager.getCache(hash);
        assertNotNull(result.get(hashFromMethod));
        assertEquals(result.get(hashFromMethod).get().toString(), returnedUrl);
    }

    @Test
    public void getOriginalUrlNotContainsInCacheTest() {
        urlRepository.save(hash, originalUrl);
        Cache result = cacheManager.getCache(hash);
        assertNull(result.get(hash));

        String returnedUrl = urlService.getOriginalUrl(hash);

        assertNotNull(result.get(hash));
        assertEquals(originalUrl, returnedUrl);
    }

    @Test
    public void getOriginalUrlThrowsExceptionTest() {
        String nonExistentHash = "nonExistentHash";

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getOriginalUrl(nonExistentHash));
    }
}
