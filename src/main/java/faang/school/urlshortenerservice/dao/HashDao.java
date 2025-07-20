package faang.school.urlshortenerservice.dao;

import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Validated
public class HashDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_UNIQUE_VALUES_SQL =
            "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

    private static final String SAVE_HASHES_SQL =
            "INSERT INTO hashes (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING";

    private static final String GET_HASHES_BATCH_SQL = """
                WITH to_delete AS (
                  SELECT hash
                  FROM hashes
                  LIMIT ?
                  FOR UPDATE SKIP LOCKED
                )
                DELETE FROM hashes
                USING to_delete
                WHERE hashes.hash = to_delete.hash
                RETURNING to_delete.hash
            """;

    public List<Long> generateSequenceValues(@Min(value = 1) int batchSize) {
        log.info("Generating unique values with a batch size {}", batchSize);
        return jdbcTemplate.queryForList(GET_UNIQUE_VALUES_SQL, Long.class, batchSize);
    }

    public void insertHashes(@NonNull List<String> hashes) {
        log.debug("Saving hashes batch with the size = {}", hashes.size());
        if (hashes.isEmpty()) {
            log.warn("The size of hashes to be saved is 0");
            return;
        }
        jdbcTemplate.batchUpdate(SAVE_HASHES_SQL, new BatchPreparedStatementSetter() {
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

    public List<String> deleteAndReturnHashes(@Min(value = 1) int batchSize) {
        log.debug("Deleting and returning hashes with a batch size {}", batchSize);
        return jdbcTemplate.queryForList(GET_HASHES_BATCH_SQL, String.class, batchSize);
    }

    public int checkStorageFilling() {
        String sql = "SELECT COUNT(*) FROM hashes";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
