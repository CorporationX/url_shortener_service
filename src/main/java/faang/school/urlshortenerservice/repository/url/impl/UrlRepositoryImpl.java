package faang.school.urlshortenerservice.repository.url.impl;

import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
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
    public Optional<String> findLongUrlByHash(String hash) {
        String sql = "SELECT long_url FROM url WHERE hash = ?";

        String url = jdbcTemplate.queryForObject(sql, new Object[]{hash}, String.class);
        return Optional.ofNullable(url);
    }

    @Override
    public List<String> retrieveAllUrlsElderOneYear() {
        String sql = """
                DELETE FROM url
                WHERE created_at < (CURRENT_TIMESTAMP - INTERVAL '1 year')
                RETURNING hash
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }
}
