package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private static final String SQL_SAVE = """
            INSERT INTO url (hash, url, created_at)
            VALUES (?, ?, NOW())
            """;

    private static final String SQL_FIND_BY_HASH = """
            SELECT url FROM url WHERE hash = ?
            """;

    private static final String SQL_DELETE_OLD = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING hash
            """;

    private final JdbcTemplate jdbcTemplate;

    public void save(String hash, String url) {
        jdbcTemplate.update(SQL_SAVE, hash, url);
    }

    public Optional<String> findByHash(String hash) {
        List<String> result = jdbcTemplate.queryForList(SQL_FIND_BY_HASH, String.class, hash);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<String> deleteOldUrls() {
        return jdbcTemplate.queryForList(SQL_DELETE_OLD, String.class);
    }
}