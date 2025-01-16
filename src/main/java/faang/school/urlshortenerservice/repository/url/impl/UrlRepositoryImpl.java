package faang.school.urlshortenerservice.repository.url.impl;

import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(String hash, String longUrl) {
        String sql = "INSERT INTO url (hash, long_url) VALUES (?, ?) " +
                "ON CONFLICT (hash) DO NOTHING";
        jdbcTemplate.update(sql, hash, longUrl);
    }

    @Override
    public Optional<String> findOriginalUrlByHash(String hash) {
        String sql = "SELECT long_url FROM url WHERE hash = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, String.class, hash));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> deleteUrlsOlderThanOneYear() {
        String sql = """
                DELETE FROM url
                WHERE created_at < (CURRENT_TIMESTAMP - INTERVAL '1 year')
                RETURNING hash
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }
}
