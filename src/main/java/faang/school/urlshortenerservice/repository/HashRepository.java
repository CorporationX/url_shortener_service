package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    private final String GET_UNIQUE_NUMBERS_SQL = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, ?)
            """;

    private final String SAVE_HASHES_SQL = """
            INSERT INTO hash(hash) VALUES(?)
            """;

    private final String GET_HASHES_SQL = """
            DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING *
            """;

    public List<Long> getUniqueNumbers(long uniqueNumbers) {
        return jdbcTemplate.queryForList(GET_UNIQUE_NUMBERS_SQL, Long.class, uniqueNumbers);
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(SAVE_HASHES_SQL, new BatchPreparedStatementSetter() {

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

    public List<String> getHashBatch(long uniqueNumber) {
        return jdbcTemplate.queryForList(GET_HASHES_SQL, String.class, uniqueNumber);
    }

}
