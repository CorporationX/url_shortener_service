package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private final JdbcTemplate jdbcTemplate;
    private String sql;

    public String save(String hash, String url, int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusHours(hours);

        sql = "INSERT INTO url (hash, url, created_at, expired_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, hash, url, Timestamp.valueOf(now), Timestamp.valueOf(expiredAt));
        return hash;
    }

    public Optional<String> getOriginalUrl(String hash) {
        sql = "SELECT url FROM url WHERE hash = ?";

        List<String> result = jdbcTemplate.query(sql, ps -> ps.setString(1, hash),
                (rs, rowNum) -> rs.getString("url")
        );

        return result.stream().findFirst();
    }

    public List<String> findAndDeleteExpiredUrls() {
        sql = "SELECT hash FROM url WHERE expired_at <= ?";
        LocalDateTime now = LocalDateTime.now();

        List<String> expiredHashes = jdbcTemplate.queryForList(
                sql,
                String.class,
                now
        );

        if (!expiredHashes.isEmpty()) {
            sql = "DELETE FROM url WHERE expired_at <= ?";
            jdbcTemplate.update(sql, now);
        }

        return expiredHashes;
    }
}
