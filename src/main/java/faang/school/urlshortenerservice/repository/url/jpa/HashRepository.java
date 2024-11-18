package faang.school.urlshortenerservice.repository.url.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int batchSize) {
        String sql = "SELECT NEXTVAL('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(1, batchSize),
                (rs, rowNum) -> rs.getLong(1)
        );
    }

    public List<String> getHashBatch(int batchSize) {
        String sql = """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash
                    FROM hash
                    ORDER BY hash
                    LIMIT ?
                )
                RETURNING hash
                """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(1, batchSize),
                (rs, rowNum) -> rs.getString("hash")
        );
    }

    public void saveAll(List<String> hashes, int batchSize) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize, (ps, hash) -> ps.setString(1, hash));
    }
}
