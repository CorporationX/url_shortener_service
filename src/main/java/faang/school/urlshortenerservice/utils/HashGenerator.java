package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    public void generateAndSaveHashes(int rangeSize) {
        long count = hashRepository.countHashes();

        int minimalHashCount = (int) (rangeSize * 0.2);

        if (count >= minimalHashCount) {
            log.info("Sufficient hashes available, skipping generation.");
            return;
        }

        log.info("Generating {} new hashes", rangeSize);

        List<Long> uniqueNumbers = generateUniqueNumbers(rangeSize);

        List<String> hashes = base62Encoder.encode(uniqueNumbers);

        String sql = "INSERT INTO hash (id, hash) VALUES (?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < hashes.size(); i++) {
            batchArgs.add(new Object[]{uniqueNumbers.get(i), hashes.get(i)});
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);

        log.info("Successfully generated and saved {} hashes", hashes.size());
    }

    private List<Long> generateUniqueNumbers(int rangeSize) {
        Query query = entityManager.createNativeQuery(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, :rangeSize)",
                Long.class
        );
        query.setParameter("rangeSize", rangeSize);

        List<Long> uniqueNumbers = query.getResultList();
        return uniqueNumbers;
    }
}
