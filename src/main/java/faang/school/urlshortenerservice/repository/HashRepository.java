package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private static final String SQL_GET_UNIQUE_NUMBERS = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, ?)
            """;
    private static final String SQL_SAVE_HASHES = """
            INSERT INTO hash (hash) VALUES (?)
            """;
    private static final String SQL_GET_HASH_BATCH = """
            DELETE FROM hash
            WHERE hash IN (SELECT hash FROM hash LIMIT ?)
            RETURNING hash
            """;

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(SQL_GET_UNIQUE_NUMBERS, Long.class, n);
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(SQL_SAVE_HASHES, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch(int n) {
        return jdbcTemplate.queryForList(SQL_GET_HASH_BATCH, String.class, n);
    }
}