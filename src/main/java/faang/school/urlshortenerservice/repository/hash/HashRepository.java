package faang.school.urlshortenerservice.repository.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private static final String GET_UNIQUE_NUMBERS_QUERY
            = "SELECT nextval('unique_number_seq') FROM generate_series(1,?)";
    private static final String SAVE_HASHES = "INSERT INTO hash (hash) VALUES (?)";
    private static final String GET_BATCH_OF_HASHES = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash
                FROM hash
                ORDER BY RANDOM()
                LIMIT ?
            )
            RETURNING *
            """;

    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(GET_UNIQUE_NUMBERS_QUERY, Long.class, n);
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(SAVE_HASHES, hashes, batchSize, (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        return jdbcTemplate.queryForList(GET_BATCH_OF_HASHES, String.class, batchSize);
    }

    public List<String> getHashWithCustomBatch(int batch) {
        return jdbcTemplate.queryForList(GET_BATCH_OF_HASHES, String.class, batch);
    }
}
