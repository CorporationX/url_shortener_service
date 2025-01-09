package faang.school.urlshortenerservice.repository.hash;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        var sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, new Object[]{n}, Long.class);
    }

    @Override
    public void save(List<String> hashes) {
        var sql = "INSERT INTO hash(hash) VALUES(?)";
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
        var sql = """
            WITH deleted AS (
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash FROM hash
                    ORDER BY random() LIMIT ?
                )
                RETURNING hash
            )
            SELECT hash FROM deleted
            """;
        return jdbcTemplate.queryForList(sql, new Object[]{n}, String.class);
    }

    @Override
    public long count() {
        var sql = "SELECT COUNT(*) FROM hash";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}