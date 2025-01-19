package faang.school.urlshortenerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

@SpringBootTest
@Testcontainers
class LiquibaseSchemaTestContainersTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        // Подменяем настройки подключения для Spring
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Указываем, где лежит наш changelog (если не прописали в application.yaml)
        registry.add("spring.liquibase.change-log",
                () -> "classpath:db/changelog/db.changelog-master.yaml");
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void testSchemaCreatedInPostgres() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // Проверяем таблицу hash
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM hash")) {
                rs.next();
                int count = rs.getInt(1);
                Assertions.assertEquals(0, count, "Table hash should be empty initially");
            }
            // Проверяем таблицу url
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM url")) {
                rs.next();
                int count = rs.getInt(1);
                Assertions.assertEquals(0, count, "Table url should be empty initially");
            }
        }
    }
}
