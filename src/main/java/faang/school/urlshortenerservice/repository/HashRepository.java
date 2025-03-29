package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${hash.batch-get-amount:1000}")
    private int getAmount;

    public List<Long> getUniqueNumbers(int amount) {
        log.info("Fetching {} unique numbers from sequence", amount);
        String sql = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, ?)
                """;

        List<Long> result = jdbcTemplate.queryForList(sql, Long.class, amount);
        log.info("Fetched {} unique numbers", result.size());
        return result;
    }

    public void saveAllHashes(List<String> hashes) {
        log.info("Saving {} hashes in batch to database", hashes.size());
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

    public List<String> getAndDeleteHashBatch() {
        log.info("Fetching and deleting {} hashes from database", getAmount);
        String sql = """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash FROM hash
                    ORDER BY RANDOM()
                    LIMIT ?
                )
                RETURNING hash
                """;

        List<String> deletedHashes = jdbcTemplate.queryForList(sql, String.class, getAmount);
        log.info("Fetched and deleted {} hashes", deletedHashes.size());
        return deletedHashes;
    }
}