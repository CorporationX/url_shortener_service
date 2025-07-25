package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepositoryJdbcImpl implements UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void save(String hash, String url) {
        String sql = "INSERT INTO url (hash, url) values (?, ?) ON CONFLICT (hash) DO NOTHING";
        jdbcTemplate.update(sql, hash, url);
    }

    @Override
    @Transactional
    public Optional<String> findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        List<String> url = jdbcTemplate.query(sql, new Object[]{hash},
                (rs, rowNum) -> rs.getString("url"));
        return url.stream().findFirst();
    }

    @Override
    public Long countExpired(String interval) {
        String sql = "SELECT COUNT (*) FROM url WHERE created_at < NOW() - CAST(? as INTERVAL)";
        return jdbcTemplate.query(
                sql,
                ps -> ps.setString(1, interval),
                rs -> rs.next() ? rs.getLong(1) : 0L
        );
    }

    @Override
    @Transactional
    public List<String> getHashesAndDelete(String interval, int cleanUpBatchSize) {
        String sql = """
                WITH to_delete AS (
                    SELECT hash FROM url
                    WHERE created_at < NOW() - CAST(? AS INTERVAL)
                    FOR UPDATE SKIP LOCKED
                    LIMIT ?)
                DELETE FROM url
                USING to_delete
                WHERE url.hash = to_delete.hash
                RETURNING url.hash
                """;
        return jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setString(1, interval);
                    ps.setInt(2, cleanUpBatchSize);
                },
                (rs, rowNum) -> rs.getString("hash")
        );
    }
}
