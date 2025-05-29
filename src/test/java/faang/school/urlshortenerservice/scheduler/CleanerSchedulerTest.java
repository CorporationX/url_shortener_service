package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.ContainersConfiguration;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ContainersConfiguration.class)
@DisplayName("CleanerScheduler Test")
class CleanerSchedulerTest {

    @Autowired
    private CleanerScheduler cleanerScheduler;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    @Transactional
    void setUp() {
        entityManager.createQuery("DELETE FROM Url").executeUpdate();
        entityManager.flush();

        jdbcTemplate.update("DELETE FROM hash", Collections.emptyMap());
    }

    @Test
    @Transactional
    @DisplayName("Delete expired URLs")
    void deleteExpiredUrls() {
        String hash = "abc123";
        String originalUrl = "https://www.example.com";
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now().minusYears(2))
                .build();

        urlRepository.save(url);
        urlCacheRepository.setUrl(hash, originalUrl);

        cleanerScheduler.deleteExpiredUrls();

        Optional<Url> foundUrl = urlRepository.findByHash(hash);
        assertFalse(foundUrl.isPresent());

        String cachedUrl = urlCacheRepository.getUrl(hash);
        assertNull(cachedUrl);

        long hashCount = hashRepository.getCountOfHashes();
        assertEquals(1, hashCount);
    }
}