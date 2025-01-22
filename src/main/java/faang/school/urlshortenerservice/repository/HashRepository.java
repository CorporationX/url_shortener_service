package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private static final String SAVE_HASH = """
            INSERT INTO hash (hash) VALUES (?);
            """;
    private static final String GET_HASHES = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash LIMIT ?
            )
            RETURNING hash;
            """;

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.batch-update-size:50}")
    private int batchSize;

    public void saveHashes(List<String> hashes) {
        jdbcTemplate.batchUpdate(SAVE_HASH, hashes, batchSize,
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashes(int amount) {
        return jdbcTemplate.queryForList(GET_HASHES, String.class, amount);
    }
}
