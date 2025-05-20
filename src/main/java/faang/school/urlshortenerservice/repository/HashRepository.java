package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch}")
    private int batchSize;

    @Transactional
    public List<Long> getUniqueNumbers(int batch) {
        String sql = "SELECT nextval('unique_number_sequence') FROM generate_series(1, ?)";

        return jdbcTemplate.queryForList(
                sql,
                Long.class,
                batch
        );
    }

    @Transactional()
    public void save(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }

        for (int i = 0; i < hashes.size(); i += batchSize) {
            List<String> batch = hashes.subList(i, Math.min(i + batchSize, hashes.size()));
            jdbcTemplate.batchUpdate(
                    "INSERT INTO hash (hash) VALUES (?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int j) throws SQLException {
                            ps.setString(1, hashes.get(j));
                        }

                        @Override
                        public int getBatchSize() {
                            return batch.size();
                        }
                    }
            );
        }
    }

    @Transactional
    public List<String> getHashBatch(int batchSize) {
        String sql = """
                DELETE FROM hash
                WHERE ctid IN (
                    SELECT ctid FROM hash
                    LIMIT ?
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING hash
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("hash"),
                batchSize
        );
    }
}
