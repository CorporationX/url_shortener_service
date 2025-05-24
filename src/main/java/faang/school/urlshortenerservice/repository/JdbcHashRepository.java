package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcHashRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Long> getNextNumbers(int count) {
        String sql = "SELECT nextval('unique_hash_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash(hash) VALUES (?)",
                hashes, 100,
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getAndRemoveBatch(int batchSize) {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}