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

    public void saveUrl(String hash, String longUrl) {
        jdbcTemplate.update("INSERT INTO url (hash, url) VALUES (?, ?)", hash, longUrl);
    }

    public List<String> deleteUrlsOlderThanOneYear() {
        String sql = "DELETE FROM url WHERE created_at < ? RETURNING hash";
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"), oneYearAgo);
    }

    public String findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return rs.getString("url");
            }
            return null;
        }, hash);
    }
}