package faang.school.urlshortenerservice.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                Long.class,
                n
        );
    }

    public void saveAllHashes(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash_value) VALUES (?)",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashBatch(int limit) {
        return jdbcTemplate.queryForList(
                """
                DELETE FROM hash
                WHERE hash_value IN (
                    SELECT hash_value FROM hash
                    LIMIT ?
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING hash_value
                """,
                String.class,
                limit
        );
    }
}