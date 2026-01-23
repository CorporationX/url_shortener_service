package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.hash.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        String sql = """
                SELECT nextval('unique_number_sequence')
                FROM generate_series(1, ?)""";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    public void save(List<String> hashes) {
        if (hashes == null ||hashes.isEmpty()) {
            log.info("No hashes to save");
            return;
        }
        String sql = """
                INSERT INTO hash (hash)
                VALUES (?)
                ON CONFLICT (hash)
                DO NOTHING""";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Transactional
    public List<String> getHashBatch(int batchSize) {
        // TABLESAMPLE SYSTEM(1) берет ~1% случайных строк (быстрее чем RANDOM())
        String sql = """
        WITH batch AS (
            SELECT hash 
            FROM hash TABLESAMPLE SYSTEM(1)
            LIMIT ? 
            FOR UPDATE SKIP LOCKED
        )
        DELETE FROM hash h
        USING batch b
        WHERE h.hash = b.hash
        RETURNING h.hash
        """;

        return jdbcTemplate.queryForList(sql, String.class, batchSize * 10);
    }

    public boolean exists(String hash) {
        String sql = "SELECT 1 FROM hash WHERE hash = ?";
        return jdbcTemplate.queryForList(sql, String.class, hash).size() > 0;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM hash";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}
