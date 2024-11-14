package faang.school.urlshortenerservice.util;

import lombok.Getter;
import org.testcontainers.containers.PostgreSQLContainer;

@Getter
public class PostgreSQLTestContainer {
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("schema_for_hashgenerator.sql");

    static {
        postgresContainer.start();
    }

    public static PostgreSQLContainer<?> getPostgresContainer() {
        return postgresContainer;
    }
}
