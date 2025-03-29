package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    public String findUrlByHash(String hash) {
        log.info("Fetching original URL from database for hash: {}", hash);
        String sql = "SELECT url FROM url WHERE hash = ?";
        return jdbcTemplate.queryForObject(sql, String.class, hash);
    }

    public void saveUrl(String hash, String url) {
        log.info("Saving URL to database: {} -> {}", hash, url);
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    public List<String> deleteOldUrlsAndReturnHashes(int days) {
        log.info("Deleting expired URLs older than {} days...", days);

        String sql = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL '? days'
            RETURNING hash
            """;

        String finalSql = sql.replace("?", String.valueOf(days));

        List<String> hashes = jdbcTemplate.queryForList(finalSql, String.class);
        log.info("Deleted {} URLs. Returned {} freed hashes.", hashes.size(), hashes.size());

        return hashes;
    }
}