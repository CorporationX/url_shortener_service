package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.TestBeans;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestBeans.class)
@DisplayName("UrlService Test")
class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Test
    @Transactional
    @DisplayName("Create and retrieve short URL")
    void createAndGetShortUrl() {
        String originalUrl = "https://www.example.com";
        UrlDto urlDto = new UrlDto(originalUrl);

        String shortUrl = urlService.createShortUrl(urlDto);
        assertNotNull(shortUrl);
        assertTrue(shortUrl.startsWith("http://localhost:8080/"));

        String hash = shortUrl.replace("http://localhost:8080/", "");

        String retrievedUrl = urlService.getUrl(hash);
        assertEquals(originalUrl, retrievedUrl);

        Optional<Url> savedUrl = urlRepository.findByHash(hash);
        assertTrue(savedUrl.isPresent());
        assertEquals(originalUrl, savedUrl.get().getUrl());

        String cachedUrl = urlCacheRepository.getUrl(hash);
        assertEquals(originalUrl, cachedUrl);
    }

    @Test
    @Transactional
    @DisplayName("Create short URL for existing URL")
    void createShortUrlForExistingUrl() {
        String originalUrl = "https://www.example.com";
        UrlDto urlDto = new UrlDto(originalUrl);

        String shortUrl1 = urlService.createShortUrl(urlDto);
        String shortUrl2 = urlService.createShortUrl(urlDto);

        assertEquals(shortUrl1, shortUrl2, "Should return the same short URL for the same original URL");
    }

    @Test
    @DisplayName("Get URL with invalid hash")
    void getUrlWithInvalidHash() {
        String invalidHash = "invalid-hash";
        assertThrows(UrlNotFoundException.class, () -> urlService.getUrl(invalidHash));
    }

    @Test
    @DisplayName("Get URL with empty hash")
    void getUrlWithNullHash() {
        assertThrows(UrlNotFoundException.class, () -> urlService.getUrl(null));
    }
}