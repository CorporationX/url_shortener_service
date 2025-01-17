package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.UrlShortenerApplicationTests;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import faang.school.urlshortenerservice.repository.url_cash.impl.UrlCacheRepositoryImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlServiceIT extends UrlShortenerApplicationTests {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlCacheRepositoryImpl urlCacheRepository;

    @Autowired
    private UrlRepositoryImpl urlRepository;

    private String hash;
    private String originalUrl;
    private String domain;

    @BeforeEach
    public void setUp() {
        hash = "hash";
        originalUrl = "https://github.com/CorporationX/url_shortener_service/pull/733/files#diff";
        domain = "http://localhost:8080/api/v1/urls/";

        String deleteFromUrlQuery = "DELETE FROM url";
        jdbcTemplate.update(deleteFromUrlQuery);

        String deleteFromHashQuery = "DELETE FROM hash";
        jdbcTemplate.update(deleteFromHashQuery);
    }

    @Test
    public void shortenUrlTest() {
        UrlDto urlDto = UrlDto.builder()
                .url(originalUrl)
                .build();

        String shortenedUrl = urlService.shortenUrl(urlDto);

        String hash = shortenedUrl.replace(domain, "");
        String savedOriginalUrl = urlRepository.findOriginalUrlByHash(hash).get();
        String savedOriginalUrlInCache = urlCacheRepository.getUrl(hash);

        assertEquals(urlDto.getUrl(), savedOriginalUrlInCache);
        assertEquals(urlDto.getUrl(), savedOriginalUrl);
    }

    @Test
    public void getOriginalUrlContainsInCacheTest() {
        urlCacheRepository.saveUrl(hash, originalUrl);

        String returnedUrl = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, returnedUrl);
    }

    @Test
    public void getOriginalUrlNotContainsInCacheTest() {
        urlRepository.save(hash, originalUrl);

        String returnedUrl = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, returnedUrl);
    }

    @Test
    public void getOriginalUrlThrowsExceptionTest() {
        String nonExistentHash = "nonExistentHash";

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getOriginalUrl(nonExistentHash));
    }
}
