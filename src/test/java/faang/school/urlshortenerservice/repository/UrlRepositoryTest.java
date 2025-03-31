package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Container
    public static PostgreSQLContainer<?> postgresqlTestContainer =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void configureDatabaseProperties(DynamicPropertyRegistry registry) {
        postgresqlTestContainer.start();

        postgresqlTestContainer.waitingFor(
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
        );

        registry.add("spring.datasource.url", postgresqlTestContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlTestContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlTestContainer::getPassword);
    }

    @Test
    public void testRemoveExpiredUrls() {
        Url firstTestUrl = new Url("hash1", "http://example1.com", LocalDateTime.now());
        Url secondTestUrl = new Url("hash2", "http://example2.com", LocalDateTime.now());

        urlRepository.saveAll(List.of(firstTestUrl, secondTestUrl));

        firstTestUrl = urlRepository.getReferenceById("hash1");
        firstTestUrl.setCreatedAt(LocalDateTime.now().minusYears(2));

        List<String> removedUrlHashes = urlRepository.deleteOldUrlsAndReturnHashes(
                LocalDateTime.now().minusYears(1)
        );

        assertEquals(1, removedUrlHashes.size());
        assertTrue(removedUrlHashes.contains("hash1"));
    }
}
