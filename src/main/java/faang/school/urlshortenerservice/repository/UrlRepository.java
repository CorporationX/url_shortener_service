package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<String> deleteOldUrlsAndReturnHashes() {
        String sql = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }

    @Transactional
    public void insertUrl(String hash,String url) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    @Transactional
    public Optional<String> findUrlByHash(String hash) {
        String sql = "SELECT * FROM url WHERE hash = ?";
        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getString("url"), hash));
    }

}