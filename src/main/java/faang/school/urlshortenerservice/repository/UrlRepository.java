package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> deleteOldLinks() {
        String sql = """
                DELETE FROM URL
                WHERE created_at<?
                RETURNING hash
                """;
        return jdbcTemplate.queryForList(sql, String.class, LocalDateTime.now().minusYears(1));
    }

    public String getUrlByHash(String hash) {
        String sql = """
                SELECT url FROM url
                WHERE hash = ?
                """;
        return jdbcTemplate.queryForObject(sql, String.class, hash);
    }

    public void saveUrlWithNewHash(String hash, String url) {
        String sql = """
                INSERT INTO url
                VALUES ( ?, ?)
                """;
        jdbcTemplate.update(sql, hash, url);
    }
}
