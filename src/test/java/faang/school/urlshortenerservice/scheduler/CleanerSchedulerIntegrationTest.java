package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.util.ContainerCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class CleanerSchedulerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = ContainerCreator.POSTGRES_CONTAINER;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Autowired
    private CleanerScheduler cleanerScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    public void testCleanObsoleteHashes() {
        cleanerScheduler.cleanObsoleteHashes();

        List<String> remainingUrls = jdbcTemplate.queryForList("SELECT hash FROM url", String.class);
        assertThat(remainingUrls).containsOnly("Uy30G1");

        List<String> hashes = jdbcTemplate.queryForList("SELECT hash_string FROM hash", String.class);
        assertThat(hashes).contains("9WH8As");
    }

    @Test
    @Transactional
    public void testNoObsoleteRecords() {
        jdbcTemplate.execute("DELETE FROM url WHERE id = 1;");

        cleanerScheduler.cleanObsoleteHashes();

        List<String> remainingUrls = jdbcTemplate.queryForList("SELECT hash FROM url", String.class);
        assertThat(remainingUrls).contains("Uy30G1");

        List<String> hashes = jdbcTemplate.queryForList("SELECT hash_string FROM hash", String.class);
        assertThat(hashes).doesNotContain("Uy30G1");
    }
}
