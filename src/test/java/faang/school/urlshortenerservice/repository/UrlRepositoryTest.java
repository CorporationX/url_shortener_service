package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.ShortUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UrlRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String urlWithRewrite = postgres.getJdbcUrl() + "?reWriteBatchedInserts=true";
        registry.add("spring.datasource.url", () -> urlWithRewrite);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UrlRepository urlRepository;

    @Test
    @DisplayName("Test clearing database of expired links: three of three deleted")
    void test_getExpiredHashes_ThreeOfThreeDeleted() {
        populateDbWithThreeEntries();
        LocalDateTime clearingDate = LocalDateTime.of(2030, 1, 25, 0,0);

        List<String> result = urlRepository.getExpiredHashes(clearingDate);

        assertNotNull(result);
        assertEquals(3, result.size());

    }

    @Test
    @DisplayName("Test clearing database of expired links: zero of three deleted")
    void test_getExpiredHashes_ZeroOfThreeDeleted() {
        populateDbWithThreeEntries();
        LocalDateTime clearingDate = LocalDateTime.of(2025, 1, 10, 0,0);

        List<String> result = urlRepository.getExpiredHashes(clearingDate);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private void populateDbWithThreeEntries() {
        ShortUrl url1 = ShortUrl.builder()
                .hash("a")
                .url("q")
                .build();
        ShortUrl url2 = ShortUrl.builder()
                .hash("b")
                .url("w")
                .build();
        ShortUrl url3 = ShortUrl.builder()
                .hash("c")
                .url("e")
                .build();
        List<ShortUrl> urls = List.of(url1, url2, url3);
        urlRepository.saveAll(urls);
    }
}