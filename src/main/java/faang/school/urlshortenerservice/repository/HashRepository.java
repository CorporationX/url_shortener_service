package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.hash.batch-size:1000}")
    private int batchSize;

    @Value("${spring.hash.fetch-size:1000}")
    private int fetchSize;

    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                new Object[]{n},
                Long.class
        );
    }

    @Transactional
    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?)",
                hashes,
                batchSize,
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    @Transactional
    public List<String> getHashBatch() {
        return jdbcTemplate.queryForList(
                "WITH deleted AS (" +
                        "   DELETE FROM hash " +
                        "   WHERE hash IN (SELECT hash FROM hash ORDER BY random() LIMIT ?) " +
                        "   RETURNING hash" +
                        ") SELECT * FROM deleted",
                String.class,
                fetchSize
        );
    }

    @Transactional
    public Long getHashSize() {
        return jdbcTemplate.queryForObject(
                "SELECT count(hash) FROM hash",
                Long.class
        );
    }
}
