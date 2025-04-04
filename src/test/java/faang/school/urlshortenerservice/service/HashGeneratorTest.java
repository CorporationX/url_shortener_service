package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.BaseIntegrationTest;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import org.awaitility.Awaitility;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HashGeneratorTest extends BaseIntegrationTest {
    @Autowired
    private HashGenerator hashGenerator;
    @Autowired
    private HashRepository hashBulkRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Session session = entityManager.unwrap(Session.class);
        Statistics statistics = session.getSessionFactory().getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }

    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void generateHashTest() {
        hashGenerator.checkHashCountsAsync();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> hashBulkRepository.count() == 900);
        Long count = hashBulkRepository.count();
        Assertions.assertEquals(900, count);
    }
}