package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<String> getOldUrlHashes(LocalDateTime cutoffDate) {
        String sql = """
                SELECT hash FROM url
                WHERE created_at < ?
                """;
        return jdbcTemplate.queryForList(sql, String.class, cutoffDate);
    }

    public List<String> getOldUrlHashesBatch(LocalDateTime cutoffDate, int limit) {
        String sql = """
                SELECT hash FROM url
                WHERE created_at < ?
                LIMIT ?
                """;
        return jdbcTemplate.queryForList(sql, String.class, cutoffDate, limit);
    }

    public void deleteUrlsByHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }
        jdbcTemplate.execute((Connection connection) -> {
            String sql = "DELETE FROM url WHERE hash = ANY(?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                java.sql.Array array = connection.createArrayOf("VARCHAR", hashes.toArray());
                ps.setArray(1, array);
                return ps.executeUpdate();
            }
        });
    }

    public String findByUrl(String url) {
        String sql = "SELECT hash FROM url WHERE url = ? LIMIT 1";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, url);
        return results.isEmpty() ? null : results.get(0);
    }

    public String findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ? LIMIT 1";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, hash);
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean save(String hash, String url) {
        String sql = """
                INSERT INTO url (hash, url, created_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """;
        
        try {
            log.debug("Saving URL to database: hash={}, url={}", hash, url);
            int rowsAffected = jdbcTemplate.update(sql, hash, url);
            boolean saved = rowsAffected > 0;
            if (saved) {
                log.debug("Successfully saved URL to database: hash={}", hash);
            } else {
                log.warn("Failed to save URL to database: hash={}, rowsAffected={}", hash, rowsAffected);
            }
            return saved;
        } catch (DataIntegrityViolationException e) {
            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("url") || msg.contains("idx_url_url_unique")) {
                    log.debug("URL already exists: {}", url);
                    return false;
                } else if (msg.contains("hash") || msg.contains("pk_url")) {
                    log.warn("Hash collision detected: {}", hash);
                    throw e; 
                }
            }
            log.error("Data integrity violation while saving URL: hash={}, error={}", hash, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while saving URL: hash={}, error={}", hash, e.getMessage(), e);
            throw e;
        }
    }

    public String findByUrlOrHash(String url, String hash) {
        String sql = "SELECT hash FROM url WHERE url = ? OR hash = ? LIMIT 1";
        List<String> results = jdbcTemplate.queryForList(sql, String.class, url, hash);
        return results.isEmpty() ? null : results.get(0);
    }

    @Deprecated
    public List<String> deleteOldUrlsAndReturnHashes(LocalDateTime cutoffDate) {
        String sql = """
                DELETE FROM url
                WHERE created_at < ?
                RETURNING hash
                """;
        return jdbcTemplate.queryForList(sql, String.class, cutoffDate);
    }
}