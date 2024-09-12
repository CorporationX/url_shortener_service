package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<Long> getUniqueNumbers(long maxRange) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, maxRange);
    }

    @Transactional
    public void saveBatchHashes(List<Hash> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES(?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i).getHash());
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Transactional
    public List<Hash> getHashBatch(long amount) {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY hash LIMIT ?) RETURNING *";
        return jdbcTemplate.queryForList(sql, Hash.class, amount);
    }

    @Transactional
    public List<Hash> cleanAndGetHashes(LocalDateTime date) {
        String sql = "DELETE FROM url WHERE created_at < ? RETURNING *";
        return jdbcTemplate.queryForList(sql, Hash.class, Timestamp.valueOf(date));
    }
    @Transactional(readOnly = true)
    public long countHashes() {
        String sql = "SELECT COUNT(*) FROM hash";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}