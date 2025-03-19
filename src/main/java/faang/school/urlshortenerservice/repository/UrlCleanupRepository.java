package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlCleanupRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> cleanupExpiredUrls() {
        return jdbcTemplate.queryForList(
                """
                WITH deleted AS (
                    DELETE FROM url 
                    WHERE deleted_at < NOW()
                    RETURNING hash
                )
                INSERT INTO hash 
                SELECT * FROM deleted
                ON CONFLICT (hash) DO NOTHING
                RETURNING hash
                """,
                String.class
        );
    }
}
