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
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)", new BatchPreparedStatementSetter() {
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

    public List<Long> getUniqueNumbers(int quantityNumbers) {
        return jdbcTemplate.query(
                "SELECT nextval('unique_number_seq') FROM pg_catalog.generate_series(1, ?)",
                new Object[]{quantityNumbers},
                (rs, rowNum) -> rs.getLong(1));
    }

    public List<String> getHashBatch(long batchSize) {
        return jdbcTemplate.queryForList(
                "DELETE FROM hash WHERE id IN (SELECT id FROM hash LIMIT ?) RETURNING hash",
                String.class,
                batchSize);
    }

    public long getHashesSize() {
        return jdbcTemplate.queryForObject("select count(*) from hash", Integer.class);
    }
}
