package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> deleteOldUrlsAndReturnHashes() {
        String sql = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }

    public void insertUrl(String hash,String url) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    public Optional<String> findUrlByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        try {
            String url = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getString("url"), hash);
            return Optional.ofNullable(url);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<String> findAllUrls() {
        String sql = "SELECT * FROM url";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }
}