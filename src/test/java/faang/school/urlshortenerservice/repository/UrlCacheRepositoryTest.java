package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.TestBeans;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestBeans.class)
@DisplayName("UrlCacheRepository Test")
class UrlCacheRepositoryTest {

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Test
    @DisplayName("Set and get URL from cache")
    void setAndGetUrl() {
        String hash = "abc123";
        String url = "https://www.example.com";

        urlCacheRepository.setUrl(hash, url);
        String retrievedUrl = urlCacheRepository.getUrl(hash);

        assertEquals(url, retrievedUrl);
    }

    @Test
    @DisplayName("Remove URL from cache")
    void removeUrl() {
        String hash = "abc123";
        String url = "https://www.example.com";

        urlCacheRepository.setUrl(hash, url);
        urlCacheRepository.removeUrl(hash);

        String retrievedUrl = urlCacheRepository.getUrl(hash);
        assertNull(retrievedUrl);
    }

    @Test
    @DisplayName("Get non-existent URL from cache")
    void getNonExistentUrl() {
        String hash = "nonexistent";
        String retrievedUrl = urlCacheRepository.getUrl(hash);
        assertNull(retrievedUrl);
    }
}