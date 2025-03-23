package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void save(String hash, String longUrl) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, longUrl);
    }

    @Override
    @Transactional
    public Optional<String> findUrlByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        String result = jdbcTemplate.queryForObject(sql,
                new Object[]{hash},
                String.class);
        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    @Transactional
    public List<String> findExpiredHashes(LocalDateTime localDateTime) {
        String sql = "DELETE FROM urls" +
                "WHERE date_column < ?" +
                "RETURNING hash;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }
}
