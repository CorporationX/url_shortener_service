package faang.school.urlshortenerservice.repository.url.impl;

import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public String findLongUrlByHash(String hash) {
        String sql = "SELECT long_url FROM url WHERE hash = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{hash}, String.class);
    }
}
