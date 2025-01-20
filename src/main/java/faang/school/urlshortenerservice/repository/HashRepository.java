package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(long numbersCount) {
        String getUniqueNumbersQuery = """
                SELECT nextval('hash_unique_number_seq') 
                FROM generate_series(1, ?)
                """;
        return jdbcTemplate.queryForList(getUniqueNumbersQuery, Long.class, numbersCount);
    }

    public void save(List<String> urlHashes) {
        String saveUrlHashesQuery = """
                INSERT INTO hash (hash)
                VALUES (?)
                """;

        jdbcTemplate.batchUpdate(
                saveUrlHashesQuery,
                urlHashes.stream()
                        .map(urlHash -> new Object[] {urlHash})
                        .toList()
        );
    }

    public List<String> getHashBatch(long batchSize) {
        String getHashBatchQuery = """
                DELETE FROM hash
                WHERE hash in (
                    SELECT hash FROM hash
                    LIMIT ?
                )
                RETURNING hash
                """;

        return jdbcTemplate.queryForList(getHashBatchQuery,
                String.class,
                batchSize
        );
    }
}
