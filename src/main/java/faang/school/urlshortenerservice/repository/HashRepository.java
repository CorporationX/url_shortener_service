package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.RetryExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RetryExecutor retryExecutor;

    @Value("${hash.batch-size}")
    private int batchSize;


    public List<Long> getUniqueNumbers(int num) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return retryExecutor.execute(() -> 
            jdbcTemplate.queryForList(sql, Long.class, num)
        );
    }

    public void save(List<String> hashes) {
        if(hashes == null || hashes.isEmpty()) {return;}
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, hashes.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return hashes.size();
                    }
                }
        );
    }

    public void delete(List<String> hashes) {
        if(hashes == null || hashes.isEmpty()) {return;}
        jdbcTemplate.execute((Connection connection) -> {
            String sql = "DELETE FROM hash WHERE hash = ANY(?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                java.sql.Array array = connection.createArrayOf("VARCHAR", hashes.toArray());
                ps.setArray(1, array);
                return ps.executeUpdate();
            }
        });
    }

    public List<String> getHashBatch(int limit) {
        String sql = """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash FROM hash
                    ORDER BY random()
                    LIMIT ?
                )
                RETURNING hash
                """;
        return retryExecutor.execute(() -> 
            jdbcTemplate.queryForList(sql, String.class, limit)
        );
    }

    public List<String> getHashBatch() {
        return getHashBatch(batchSize);
    }

    public List<String> getHashBatchAtomic(int limit) {
        String sql = """
                UPDATE hash
                SET available = false
                WHERE hash IN (
                    SELECT hash FROM hash
                    WHERE available = true
                    LIMIT ?
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING hash
                """;
        return retryExecutor.execute(() -> 
            jdbcTemplate.queryForList(sql, String.class, limit)
        );
    }

    public void saveAsUsed(String hash) {
        String sql = """
                INSERT INTO hash (hash, available)
                VALUES (?, false)
                ON CONFLICT (hash) DO UPDATE SET available = false
                """;
        retryExecutor.execute(() -> {
            jdbcTemplate.update(sql, hash);
            return null;
        });
    }

    public List<String> getAvailableHashes(int limit) {
        String sql = """
                SELECT hash FROM hash
                WHERE available IS NULL OR available = true
                ORDER BY random()
                LIMIT ?
                """;
        return jdbcTemplate.queryForList(sql, String.class, limit);
    }
}