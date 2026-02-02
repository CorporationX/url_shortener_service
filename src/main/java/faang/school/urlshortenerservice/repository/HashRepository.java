package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveHashes(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?) ON CONFLICT DO NOTHING",
                hashes,
                hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    @Transactional
    public List<String> getAndDeleteHashes(int limit) {
        return jdbcTemplate.queryForList(
                """
                        DELETE FROM hash
                        WHERE hash IN (
                            SELECT hash
                            FROM hash
                            LIMIT ?
                        )
                        RETURNING hash
                        """,
                String.class,
                limit
        );
    }
}