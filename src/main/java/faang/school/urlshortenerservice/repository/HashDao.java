package faang.school.urlshortenerservice.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashDao {
    private final JdbcTemplate jdbcTemplate;

    public boolean tryLock(int key) {
        String sql = "SELECT pg_try_advisory_lock(?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, key);
    }

    public void unlock(int key) {
        String sql = "SELECT pg_advisory_unlock(?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, key);
    }

    public long countHashes() {
        String sql = "SELECT COUNT(*) FROM hash";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return (count != null) ? count : 0L;
    }

    public List<Long> getUniqueNumbers(int batch) {
        if (batch <= 0) {
            return Collections.emptyList();
        }
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, batch);
    }

    public void save(@NonNull List<String> hashes) {
        if (hashes.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    public List<String> getHashBatch(int batch) {
        if (batch <= 0) {
            return Collections.emptyList();
        }
        String sql = """
                WITH selected_hashes AS (
                    SELECT hash
                    FROM hash
                    LIMIT ?
                    FOR UPDATE SKIP LOCKED
                )
                DELETE FROM hash
                WHERE hash IN (SELECT hash FROM selected_hashes)
                RETURNING hash;
                """;
        return jdbcTemplate.queryForList(sql, String.class, batch);
    }
}
