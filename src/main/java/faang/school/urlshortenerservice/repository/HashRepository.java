package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<Long> getUniqueNumbers(long maxRange) {
        String sql = "SELECT nextval('unique_numbers_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, maxRange);
    }

    @Transactional
    public void save(List<Hash> hashes) {
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
}
