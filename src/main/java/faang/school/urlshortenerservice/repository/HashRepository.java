package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> getHashBatch(int count) {
        String sql = """
                WITH selected_hashes AS (
                    SELECT hash 
                    FROM hash 
                    ORDER BY RANDOM() 
                    LIMIT ?
                    FOR UPDATE SKIP LOCKED
                )
                DELETE FROM hash 
                USING selected_hashes 
                WHERE hash.hash = selected_hashes.hash 
                RETURNING hash.hash
                """;

        return jdbcTemplate.queryForList(sql, String.class, count);
    }

    public int getHashesCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Integer.class);
    }

    public List<Long> getNextSequenceValues(int count) {
        return jdbcTemplate.queryForList(
                "SELECT nextval('url_hash_seq') FROM generate_series(1, ?)",
                Long.class,
                count
        );
    }

    public void saveHashes(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?)",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }
}

