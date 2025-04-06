package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public void save(String hash, String url) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    public String findByHash(String hash) {
        String sql = "SELECT * FROM url WHERE hash = ?";
        return jdbcTemplate.queryForObject(sql, String.class, hash);
    }

    public List<String> deleteOldUrlsAndReturnHashes() {
        String sql = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }
}