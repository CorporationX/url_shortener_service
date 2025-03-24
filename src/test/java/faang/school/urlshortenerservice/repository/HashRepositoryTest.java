package faang.school.urlshortenerservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        POSTGRESQL_CONTAINER.waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
        );

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    @Sql("/test-hash-table.sql")
    public void testGetHashes() {
        long initialCount = hashRepository.count();
        assertThat(initialCount).isEqualTo(3);

        List<String> hashes = hashRepository.getAndRemoveHashes(2);

        assertThat(hashes).hasSize(2);
        assertThat(hashes).contains("hash1", "hash2");

        long remainingCount = hashRepository.count();
        assertThat(remainingCount).isEqualTo(1);
    }

    @Test
    public void testGetUniqueNumbers() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(3);

        assertThat(uniqueNumbers).hasSize(3);
        assertThat(uniqueNumbers).doesNotHaveDuplicates();
    }

}
