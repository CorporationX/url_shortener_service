package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong(1), n);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(), (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch(int batchSize) {
        String sql = "DELETE FROM hash WHERE hash IN (" +
                "SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?" +
                ") RETURNING hash";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"), batchSize);
    }
}