package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Testcontainers
class CleanSchedulerTest {

    @Autowired
    private HashRepository hashRepository;
    @Autowired
    private UrlRepository urlRepository;

    Url expiredUrl;
    Url notExpiredUrl;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {

        expiredUrl = Url.builder()
                .hash("h11111")
                .url("http://expired.ru/")
                .expiredAtDate(LocalDateTime.now().minusDays(1))
                .build();
        notExpiredUrl = Url.builder()
                .hash("h22222")
                .url("http://alive.ru/")
                .expiredAtDate(LocalDateTime.now().plusDays(10))
                .build();
        urlRepository.save(expiredUrl);
        urlRepository.save(notExpiredUrl);

    }

    @Test
    @DisplayName("Test deletion of expired urls")
    void testDeleteExpiredUrls() {
        List<Url> urlsBefore = urlRepository.findAll();
        Assertions.assertEquals(2, urlsBefore.size());

        urlRepository.deleteExpiredUrls();

        List<Url> urlsAfter = urlRepository.findAll();
        Assertions.assertEquals(1, urlsAfter.size());
        Assertions.assertEquals(notExpiredUrl.getUrl(), urlsAfter.get(0).getUrl());
    }
}