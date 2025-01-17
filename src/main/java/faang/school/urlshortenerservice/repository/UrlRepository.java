package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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

    private final JdbcTemplate jdbcTemplate;

    public String findByHash(String hash) {
        return jdbcTemplate.queryForObject(FIND_BY_HASH, String.class, hash);
    }

    public void saveUrl(String hash, String url) {
        jdbcTemplate.update(SAVE_URL, hash, url, LocalDateTime.now());
    }
}
