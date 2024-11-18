package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomHashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final HashRepository hashRepository;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    public void saveHashesBatch(List<String> hashes) {
        int batchCount = (int) Math.ceil((double) hashes.size() / batchSize);
        for (int i = 0; i < batchCount; i++) {
            List<String> batch = hashes.subList(i * batchSize, Math.min((i + 1) * batchSize, hashes.size()));
            List<Hash> hashEntities = batch.stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashEntities);
        }
    }

    public List<String> getHashBatch(int n) {
        String sql = "WITH deleted AS ( " +
                "  DELETE FROM hash " +
                "  WHERE hash IN ( " +
                "    SELECT hash FROM hash ORDER BY RANDOM() LIMIT ? " +
                "  ) " +
                "  RETURNING hash " +
                ") " +
                "SELECT hash FROM deleted";

        return jdbcTemplate.queryForList(sql, String.class, n);
    }
}