package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.BaseIntegrationTest;
import faang.school.urlshortenerservice.entity.Hash;
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
    private HashRepository hashRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ChangeHashSequenceRepository changeHashSequenceRepository;

    @Test
    void getUniqueNumbers() {
        List<Long> numbers = hashRepository.getUniqueNumbers(1000);
        Assertions.assertEquals(1000, numbers.size());
    }

    @Test
    void saveHashes() {
        Session session = entityManager.unwrap(Session.class);
        Statistics statistics = session.getSessionFactory().getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        List<Hash> hashes = IntStream.range(0, 1000)
                .boxed()
                .map(i -> Hash.builder()
                        .hash("hash_%s".formatted(i))
                        .build())
                .toList();
        hashRepository.saveAll(hashes);
        Assertions.assertEquals(1000, hashRepository.count());
    }


    @Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/insert-hashes.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getHashBatch() {
        List<String> hashList = hashRepository.getHashBatch(3);
        Assertions.assertEquals(3, hashList.size());
    }

    @Test
    void getMinSequence() {
        long min = 14776336;
        changeHashSequenceRepository.setSequenceMinValue(min);
        long actual = hashRepository.getSequenceMin();
        Assertions.assertEquals(14776336, actual);
    }

    @Test
    void getMaxSequence() {
        long max = 916132832;
        changeHashSequenceRepository.setSequenceMaxValue(max);
        long actual = hashRepository.getSequenceMax();
        Assertions.assertEquals(916132832, actual);
    }
}