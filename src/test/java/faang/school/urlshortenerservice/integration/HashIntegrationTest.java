package faang.school.urlshortenerservice.integration;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.impl.HashServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class HashIntegrationTest {

    @Autowired
    private HashRepository hashRepository;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

            //.withInitScript("initDB.sql");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HashServiceImpl hashGenerator;

    @Test
    @DisplayName("Test scenario for hash generating")
    public void testGenerateHashes() {
/*        int size = 2;
        //TODO переделать цифры исходя из Yaml
        List<Hash> result = hashGenerator.generateHashes(size);
        List<Hash> result1 = hashGenerator.generateHashes(size);


        List<Hash> res = result.get();
        Assertions.assertEquals(10, res.size());
        List<Hash> res1 = result1.get();
        Assertions.assertEquals(10, res1.size());

        List<Hash> hashes = hashRepository.findAll();
        assertThat(hashes).hasSize(20);
*/
    }
}
