package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private static final String SAVE_URL = """
            INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?);
            """;
    private static final String FIND_BY_HASH = """
            SELECT url
            FROM url
            WHERE hash = ?;
            """;
    private static final String REMOVE_OLD_URLS = """
            DELETE FROM url
            WHERE extract(day from CURRENT_TIMESTAMP - created_at) > ?
            RETURNING hash;
            """;

    private final JdbcTemplate jdbcTemplate;

    public String findByHash(String hash) {
        return jdbcTemplate.queryForObject(FIND_BY_HASH, String.class, hash);
    }

    public void saveUrl(String hash, String url) {
        jdbcTemplate.update(SAVE_URL, hash, url, LocalDateTime.now());
    }

    public List<String> removeOldUrls(int days) {
        return jdbcTemplate.queryForList(REMOVE_OLD_URLS, String.class, days);
    }
}
