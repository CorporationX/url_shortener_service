package faang.school.urlshortenerservice.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int count) {
        return jdbcTemplate.queryForList(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                Long.class,
                count
        );
    }

    public void saveAllHashes(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?)",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashBatch(int limit) {
        return jdbcTemplate.queryForList(
                """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash FROM hash
                    LIMIT ?
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING hash
                """,
                String.class,
                limit
        );
    }
}