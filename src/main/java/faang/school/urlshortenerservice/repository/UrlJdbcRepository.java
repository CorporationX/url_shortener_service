package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlJdbcRepository implements UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public List<String> deleteOldUrlAndReturnHashes() {
        String sql = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Transactional
    @Override
    public void save(String url, String hash) {
        String sql = "INSERT INTO url (url, hash, created_at) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, url, hash, LocalDateTime.now());
    }

    @Override
    public Optional<String> getUrlByHash(String hash) {
        String sql = "SELECT long_url FROM url WHERE hash = ?";
        return Optional.of(jdbcTemplate.queryForObject(sql, String.class, hash));
    }
}
