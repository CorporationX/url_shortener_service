package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAsync
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HashCacheIntegrationTest {

    @Autowired
    private HashCache hashCache;
    @Autowired
    private HashRepository hashRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:14");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("When getOneHash called retrieve one hash from cache, if it's less than fill percent generate hash")
    public void whenGetOneHashCalledThenRetrieveHashFromCacheAndGenerateHashesIfNecessary()
            throws InterruptedException {
        Thread.sleep(1000);

        String resultHashB = hashCache.getOneHash();
        assertEquals("b", resultHashB);
        String resultHashC = hashCache.getOneHash();
        assertEquals("c", resultHashC);
        String resultHashD = hashCache.getOneHash();
        assertEquals("d", resultHashD);
        String resultHashE = hashCache.getOneHash();
        assertEquals("e", resultHashE);
        String resultHashF = hashCache.getOneHash();
        assertEquals("f", resultHashF);

        Thread.sleep(1000);
        int size = hashRepository.findAll().size();

        assertEquals(size, 200);
    }
}
