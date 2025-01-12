package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    @Value("${url.hash.get-count}")
    private int hashGetCount;

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int numbersCount) {
        String getUniqueNumbersQuery = """
                SELECT nextval(hash_unique_number_seq) 
                FROM generate_series(1, ?); 
                """;
        return jdbcTemplate.queryForList(getUniqueNumbersQuery, Long.class, numbersCount);
    }

    public void save(List<String> urlHashs) {
        String saveUrlHashsQuery = """
                INSERT INTO hash (hash)
                VALUES (?)
                """;

        jdbcTemplate.batchUpdate(
                saveUrlHashsQuery,
                urlHashs.stream()
                        .map(urlHash -> new Object[] { urlHash })
                        .toList()
        );
    }

    public List<String> getHashBatch() {
        String getHashBatchQuery = """
                DELETE FROM hash
                WHERE hash in (
                    SELECT hash 
                    FROM hash
                    LIMIT ?
                )
                RETURNING hash;
                """;

        return jdbcTemplate.queryForList(getHashBatchQuery, String.class, hashGetCount);
    }
}
