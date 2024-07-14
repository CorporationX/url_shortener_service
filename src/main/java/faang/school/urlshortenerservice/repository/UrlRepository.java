package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(String hash, String url) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    public Optional<String> findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        return Optional.of(jdbcTemplate.queryForObject(sql, new Object[]{hash}, String.class));
    }

    public List<String> deleteAndGetOldHash() {
        String sql = "DELETE FROM url WHERE created_at < now() - interval '1 year' RETURNING hash";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getString("hash")));
    }

}
