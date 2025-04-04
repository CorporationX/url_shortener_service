package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlCleanupRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> findExpiredHashes() {
        return jdbcTemplate.queryForList(
                "SELECT hash FROM url WHERE deleted_at < NOW() FOR UPDATE SKIP LOCKED",
                String.class
        );
    }

    public void deleteExpiredHashes(List<String> hashes) {
        if (!hashes.isEmpty()) {
            jdbcTemplate.update(
                    "DELETE FROM url WHERE hash = ANY(?)",
                    hashes.toArray()
            );
        }
    }

    public void saveHashesToPool(List<String> hashes) {
        if (!hashes.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING",
                    hashes,
                    hashes.size(),
                    (ps, hash) -> ps.setString(1, hash)
            );
        }
    }
}
