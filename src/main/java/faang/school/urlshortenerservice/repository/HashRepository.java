package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Integer> getNUniqueNumbers(long n) {
        log.info("Trying to get {} unique numbers", n);
        String sql = String.format("SELECT NEXTVAL('unique_number_seq') FROM GENERATE_SERIES(1, %d)", n);
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    @Transactional
    public void save(List<String> hashes) {
        log.info("Trying to save hashes in database");
        String sql = "INSERT INTO hash(hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String hash = hashes.get(i);
                ps.setString(1, hash);
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Transactional
    public List<String> getHashBatch(long batchSize) {
        log.info("Trying to get {} hashes", batchSize);
        String sql = "DELETE FROM hash WHERE ctid IN (SELECT ctid FROM hash LIMIT ? FOR UPDATE SKIP LOCKED) RETURNING *";
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
