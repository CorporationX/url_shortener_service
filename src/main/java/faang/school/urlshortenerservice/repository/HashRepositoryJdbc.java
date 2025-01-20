package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.persist.batch-size}")
    private int batchSize;

    /**
     * Get a batch of unique numbers from the sequence
     */
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_numbers_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    /**
     * Save a batch of hashes to the table
     */
    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, hashes, batchSize, (ps, hash) -> ps.setString(1, hash));
    }

    /**
     * Get a batch of hashes and delete them from the table
     */
    public List<String> getHashBatch(int refillSize) {
        String sql = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash
                FROM hash
                ORDER BY random()
                LIMIT ?
            )
            RETURNING hash
        """;

        return jdbcTemplate.queryForList(sql, String.class, refillSize);
    }
}