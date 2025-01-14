package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers(long maxRange) {
        String sql = """
                SELECT nextval('unique_hash_number_seq')
                FROM generate_series(1, ?)
                """;

        return jdbcTemplate.query(sql,
                ps -> ps.setLong(1, maxRange),
                (rs, rowNum) -> rs.getLong(1)
        );
    }

    @Override
    public void saveAll(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES(?)";
        jdbcTemplate.batchUpdate(sql,
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    @Override
    public List<String> getHashBatchAndDelete(long amount) {
        String sql = """
                DELETE FROM hash
                 WHERE id IN (
                    SELECT id
                    FROM hash
                    ORDER BY id
                     LIMIT ?
                ) RETURNING hash
                """;
        return jdbcTemplate.query(sql,
                ps -> ps.setLong(1, amount),
                (rs, rowNum) -> rs.getString("hash")
        );
    }
}
