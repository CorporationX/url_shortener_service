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

    public Optional<String> findByHash(String hash) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT url FROM url WHERE hash = ?",
                    String.class, hash));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(String hash, String url) {
        jdbcTemplate.update("INSERT INTO url(hash, url) VALUES (?, ?)", hash, url);
    }

    public List<String> deleteOldAndReturnHashes() {
        return jdbcTemplate.queryForList("""
                DELETE FROM url 
                WHERE created_at < CURRENT_DATE - INTERVAL '1 YEAR'
                RETURNING hash
                """, String.class);
    }

    public boolean existsByHash(String hash) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM url WHERE hash = ?",
                Integer.class,
                hash
        );
        return count != null && count > 0;
    }
}
