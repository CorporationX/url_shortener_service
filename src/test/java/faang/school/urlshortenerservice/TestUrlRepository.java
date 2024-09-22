package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TestUrlRepository {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2");

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setUp() {
        postgreSQLContainer.start();

    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void testFindByHash() {
        String url = "https://www.google.com";
        String key = "url";
        urlRepository.saveAssociation(url, key);
        String retrievedUrl = urlRepository.findByHash(key);
        assertThat(retrievedUrl).isEqualTo(url);
    }

    @Test
    public void testFindByHashNotFound() {
        String url = "https://www.google.com";
        String key = "url";
        assertThrows(EmptyResultDataAccessException.class, () -> urlRepository.findByHash(key));
    }

    @Test
    public void testFindByUrl() {
        String url = "https://www.google.com";
        String key = "url";
        urlRepository.saveAssociation(url, key);
        Optional<String> retrievedHash = urlRepository.findByUrl(url);
        assertThat(key).isEqualTo(retrievedHash.get());
    }

    @Test
    public void testFindByUrlNotFound() {
        String url = "https://www.google.com";
        String key = "url";
        assertThat(urlRepository.findByUrl(url)).isEmpty();
    }

    @Test
    public void testSaveAssociation() {
        String url = "https://www.google.com";
        String key = "url";
        int result = urlRepository.saveAssociation(url, key);
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testUnusedHashesFromMonthsPeriod() {
        String url = "https://www.google.com";
        String key = "url";
        Period period = Period.ofMonths(12);
        urlRepository.saveAssociation(url, key);
        jdbcTemplate.update("update url set created_at = created_at - INTERVAL '12 months' where url.hash = ?", key);
        List<String> retrievedHashes = urlRepository.getUnusedHashesForPeriod(period);
        assertThat(retrievedHashes).hasSize(1);
    }

    @Test
    public void testUnusedHashesFromMonthsPeriodNotFount() {
        String url = "https://www.google.com";
        String key = "url";
        Period period = Period.ofMonths(12);
        urlRepository.saveAssociation(url, key);
        assertThat(urlRepository.getUnusedHashesForPeriod(period)).isEmpty();
    }

}
