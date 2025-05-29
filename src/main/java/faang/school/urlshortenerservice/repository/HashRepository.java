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

    @Value("${hash.batch.repository.size:100}")
    private int batchSize;

    @Transactional
    public List<String> getHashBatch(int quantity) {
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
        return jdbcTemplate.queryForList(sql, String.class, quantity);
    }

    @Transactional
    public void save(List<String> hashes) {
        String sql = """
                INSERT INTO hash (hash)
                VALUES (?)
                ON CONFLICT DO NOTHING
                """;
        jdbcTemplate.batchUpdate(
                sql,
                hashes,
                batchSize,
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    @Transactional(readOnly = true)
    public long getHashCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Long.class);
    }
}
