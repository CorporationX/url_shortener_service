package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HashRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private HashRepository hashBulkRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void getUniqueNumbers() {
        List<Long> numbers = hashBulkRepository.getUniqueNumbers(1000);
        Assertions.assertEquals(1000, numbers.size());
    }

    @Test
    void saveHashes() {
        Session session = entityManager.unwrap(Session.class);
        Statistics statistics = session.getSessionFactory().getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        List<String> hashes = IntStream.range(100, 1000)
                .boxed()
                .map("ha%s"::formatted)
                .toList();
        hashBulkRepository.saveAll(hashes);
        Assertions.assertEquals(900, hashBulkRepository.count());
    }


    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-hashes.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getHashBatch() {
        List<String> hashList = hashBulkRepository.getHashBatch(3);
        Assertions.assertEquals(3, hashList.size());
    }
}