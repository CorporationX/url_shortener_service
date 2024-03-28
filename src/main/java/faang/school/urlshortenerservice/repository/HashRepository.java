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

    public void saveAll(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash(hash) VALUES(?)",
                new BatchPreparedStatementSetter() {
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

    public List<Long> getUniqueNumbers(long uniqueNumbers) {
        return jdbcTemplate.queryForList("DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING *",
                Long.class,
                uniqueNumbers);
    }

    public List<String> getHashBatch(long uniqueNumber) {
        return jdbcTemplate.queryForList("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                String.class,
                uniqueNumber);
    }
}