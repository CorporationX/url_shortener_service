package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TestHashRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HashRepository hashRepository;
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
    public void testGetUniqueNumbers() {
        int amount = 2;
        List<Long> numbers = hashRepository.getUniqueNumbers(amount);
        assertThat(numbers.size()).isEqualTo(amount);
    }

    @Test
    public void testSaveHashes() {
        List<String> hashes = new ArrayList<>();
        hashes.addAll(Arrays.asList("a", "b", "c", "d"));
        int[] result = hashRepository.saveHashes(hashes);
        assertThat(result.length).isEqualTo(hashes.size());
    }

    @Test
    public void testSaveHashesFailed() {
        List<String> hashes = new ArrayList<>();
        int[] result = hashRepository.saveHashes(hashes);
        assertThat(result.length).isEqualTo(0);
    }

    @Test
    public void testGetAndDeleteHashes() {
        int amount = 2;
        List<String> hashes = new ArrayList<>();
        hashes.addAll(Arrays.asList("a", "b", "c", "d"));
        hashRepository.saveHashes(hashes);
        List<String> deletedHashes = hashRepository.getAndDeleteHashes(amount);
        assertThat(deletedHashes.size()).isEqualTo(amount);
    }
}
