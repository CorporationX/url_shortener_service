package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    public String get(String hash) {
        String sql = "SELECT hash FROM hash WHERE hash = ?";
        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                hash
        );
    }

    public void saveBatch(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(
                sql,
                hashes,
                hashes.size(),
                (ps, argument) -> ps.setString(1, argument)
        );
    }

    public List<String> deleteAndGetBatch(int uniqueNumbersBatch) {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(1, uniqueNumbersBatch),
                (rs, rowNum) -> rs.getString("hash")
        );
    }
}
