package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch.size:50}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be a positive number");
        }

        return jdbcTemplate.query(
                "SELECT nextval('unique_number_seq') AS num FROM generate_series(1, ?) ORDER BY RANDOM()",
                (rs, rowNum) -> rs.getLong("num"),
                count
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?)",
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<String> getHashBatch() {
        jdbcTemplate.execute("SELECT pg_advisory_xact_lock(hashtext('hash_table_batch_operation')::bigint)");

        return jdbcTemplate.query(
                "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?) RETURNING hash",
                (rs, rowNum) -> rs.getString("hash"),
                batchSize
        );
    }
}
