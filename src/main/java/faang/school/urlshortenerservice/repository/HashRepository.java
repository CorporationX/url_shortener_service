package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-get-amount:10000}")
    private long getAmount;

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";

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

    public List<Long> getUniqueNumbers(long amount) {
        String sql = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, ?)
            """;

        return jdbcTemplate.queryForList(sql, Long.class, amount);
    }

    public List<String> getHashBatch() {
        String sql = """
                    DELETE
                    FROM hash
                    WHERE hash IN (
                        SELECT hash
                        FROM hash
                        LIMIT ?
                    )
                    RETURNING hash
                """;

        return jdbcTemplate.queryForList(sql, String.class, getAmount);
    }

    public Long getCurrentHashAmount() {
        String sql = """
                SELECT COUNT(*)
                FROM hash
                """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
