package faang.school.urlshortenerservice.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size}")
    private int batchSize;
    
    public List<Long> getUniqueNumbers(int amount) {
        return jdbcTemplate.queryForList(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                Long.class,
                amount
                );
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?)",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
                );
    }

    public List<String> getHashBatch() {
        return jdbcTemplate.queryForList(
                """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash
                    FROM hash
                    ORDER BY random()
                    LIMIT ?
                    )
                RETURNING hash
                """,
                String.class,
                batchSize
                );
    }
}

