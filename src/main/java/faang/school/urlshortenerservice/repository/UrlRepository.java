package faang.school.urlshortenerservice.repository;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Timed(value = "find_by_hash_timer", description = "Time taken to find URL by hash in PostgreSQL",
            histogram = true, percentiles = {0.5, 0.95})
    @Transactional
    public Optional<String> findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash=?";

        try {
            String url = jdbcTemplate.queryForObject(sql, String.class, hash);
            return Optional.ofNullable(url);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Timed(value = "save_to_url_repository_timer", description = "Time taken to save to URL repository",
            histogram = true, percentiles = {0.5, 0.95})
    @Transactional
    public void save(String hash, String url) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    @Transactional
    public List<String> deleteOldUrl() {
        String sql = """
                DELETE FROM url
                WHERE created_at < NOW() - INTERVAL '1 year'
                RETURNING hash
                """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

}
