package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_UNIQUE_NUMBER_QUERY =
            "SELECT nextval('hash_unique_number_seq') FROM generate_series(1, ?)";

    private static final String SAVE_BATCH_QUERY = "INSERT INTO hash (hash) VALUES(?)";

    private static final String GET_HASH_BATCH_QUERY =
            """ 
                    DELETE FROM hash
                    WHERE hash IN (SELECT hash FROM hash ORDER BY random() LIMIT ?)
                    RETURNING hash
                    """;

    @Override
    public List<Long> getUniqueNumbers(int number) {
        return jdbcTemplate.queryForList(GET_UNIQUE_NUMBER_QUERY, Long.class, number);
    }

    @Override
    public void saveBatch(List<String> hashes) {
        jdbcTemplate.batchUpdate(SAVE_BATCH_QUERY, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash));
    }

    @Override
    public List<String> getHashBatch(int number) {
        return jdbcTemplate.queryForList(GET_HASH_BATCH_QUERY, String.class, number);
    }
}
