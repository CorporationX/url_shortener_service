package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> getHashesWithExpiredDates(String interval) {
        log.info(interval);
        String sql = "DELETE FROM url WHERE created_at < NOW() - ?::INTERVAL RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, interval);
    }

    public void save(String hash, String url) {
        String sql = "INSERT INTO url(hash, url) VALUES(?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    public String getUrlByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, hash);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getHashByLongUrl(String longUrl) {
        String sql = "SELECT hash FROM url WHERE url = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, longUrl);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

