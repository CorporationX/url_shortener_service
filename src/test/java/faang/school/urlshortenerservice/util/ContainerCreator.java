package faang.school.urlshortenerservice.util;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainerCreator {

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("testdb")
                    .withUsername("admin")
                    .withPassword("admin")
                    .withInitScript("schema_for_url_controller.sql");

    static {
        POSTGRES_CONTAINER.start();
    }
}
