package faang.school.urlshortenerservice.repository.hash;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch_size}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        String sql = "SELECT nextval('unique_hash_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

    @Override
    public void save(List<String> hashes) {
        if (hashes.isEmpty()) {
            log.debug("Empty hashes List");
            return;
        }
        log.info("Saving {} hashes (batch size: {})", hashes.size(), batchSize);
        String sql = "INSERT INTO hashes(hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize, (ps, hash)
                -> ps.setString(1, hash));
    }

    @Override
    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM hashes
                WHERE hash IN(
                 SELECT hash FROM hashes TABLESAMPLE BERNOULLI(10)
                 LIMIT ?
                 FOR UPDATE SKIP LOCKED
                )
                RETURNING hash
                """;
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}