package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = "spring.test.database.replace=none")
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;
    String urlOrigin = "https://ya.ru";
    String urlOrigin1 = "https://yand.ru";

    @Container
    public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withInitScript("init1.sql");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRE_SQL_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @Test
    void findUrlByUrl() {
        //Arrange
        Url url = new Url("hash1", urlOrigin, LocalDateTime.now());
        urlRepository.save(url);

        //Act
        Url result = urlRepository.findUrlByUrl(urlOrigin);

        //Assert
        assertTrue(POSTGRE_SQL_CONTAINER.isRunning());
        assertEquals(result.getUrl(), url.getUrl());
        assertEquals(result.getHash(), url.getHash());
    }

    @Test
    void deleteOldUrl() {
        String hashOld = "hash1";
        Url urlOld = new Url(hashOld, urlOrigin, LocalDateTime.now());
        Url saved = urlRepository.save(urlOld);
        saved.setCreatedAt(LocalDateTime.now().minusYears(2));
        String hashNew = "hash2";
        Url urlNew = new Url(hashNew, urlOrigin1, LocalDateTime.now());
        urlRepository.save(urlNew);

        //Act
        List<String> result = urlRepository.deleteOldUrl();

        //Assert
        assertNotNull(result);
        assertTrue(result.contains(hashOld));
        assertFalse(result.contains(hashNew));
    }
}