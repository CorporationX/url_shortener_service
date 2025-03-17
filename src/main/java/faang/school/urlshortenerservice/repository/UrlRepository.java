package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> getHashesWithExpiredDates(String interval) {
        String sql = "DELETE FROM url WHERE created_at < NOW() - INTERVAL ? RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, interval);
    }
}
