package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.limit.factor}")
    private int limitFactor;

    @Value("${hash.batch.size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(long number) {
        if (number <= 0) {
            return Collections.emptyList();
        }
        String sql = "SELECT nextval('unique_number_seq')  FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, Math.min(number, batchSize * limitFactor));
    }

    public void save(List<String> hashes) {
        if (hashes.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize, (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch(long batch) {
        String sql = "DELETE FROM hash WHERE ctid IN (SELECT ctid FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"),batch);
    }

    public long countAvailableHashes() {
        String sql = "SELECT COUNT(hash) FROM hash";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
