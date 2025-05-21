package faang.school.urlshortenerservice.repository.implementations;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.interfaces.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<String> deleteOlderThan(LocalDateTime threshold) {
        log.info("Deleting URLs older than: {}", threshold);
        return jdbcTemplate.query(
                "DELETE FROM url WHERE created_at < ? RETURNING hash",
                (rs, rowNum) -> rs.getString("hash"),
                Timestamp.valueOf(threshold)
        );
    }

    @Override
    public Optional<UrlDto> findByHash(String hash) {
        log.info("Finding URL by hash: {}", hash);
        List<UrlDto> results = jdbcTemplate.query(
                "SELECT hash, url, created_at FROM url WHERE hash = ?",
                (rs, rowNum) -> new UrlDto(
                        rs.getString("hash"),
                        rs.getString("url"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ),
                hash
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<UrlDto> findByUrl(String url) {
        log.info("Finding URL by url: {}", url);
        List<UrlDto> results = jdbcTemplate.query(
                "SELECT hash, url, created_at FROM url WHERE url = ?",
                (rs, rowNum) -> new UrlDto(
                        rs.getString("hash"),
                        rs.getString("url"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ),
                url
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void save(String hash, String url) {
        log.info("Saving URL with hash: {} and url: {}", hash, url);
        jdbcTemplate.update(
                "INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)",
                hash,
                url,
                Timestamp.valueOf(LocalDateTime.now())
        );
    }
}
