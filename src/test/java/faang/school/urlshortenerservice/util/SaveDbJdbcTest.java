package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class SaveDbJdbcTest {

    @Autowired
    private SaveDbJdbc saveDbJdbc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static PostgreSQLContainer<?> postgresContainer;

    @BeforeAll
    public static void setup() {
        postgresContainer = PostgreSQLTestContainer.getPostgresContainer();
    }

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Test
    public void testSaveDbJdbc() {
        List<String> hashes = List.of("101", "102", "103");

        saveDbJdbc.save(hashes);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash WHERE hash IN ('101', '102', '103')", Integer.class);
        assertThat(count).isEqualTo(3);
    }
}