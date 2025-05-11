package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT next val('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Override
    public void saveHashes(List<String> hashes) {
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

    @Override
    public List<String> getHashBatch(int n) {
        String sql = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash ORDER BY random() LIMIT ?
            )
            RETURNING hash
        """;
        return jdbcTemplate.queryForList(sql, String.class, n);
    }
}
