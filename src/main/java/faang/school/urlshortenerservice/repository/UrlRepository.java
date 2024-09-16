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

    public void save(String hash, String longUrl) {
        String sql = "INSERT INTO url (hash, url) values (?, ?)";
        jdbcTemplate.update(sql, hash, longUrl);
    }

    public String findUrl(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        List<String> results = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getString("url"),
                hash);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public List<String> deleteUrlsAndFreeHashes(LocalDateTime beforeDate) {
        String sql = "DELETE FROM url WHERE created_at < ? RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, beforeDate);
    }
}
