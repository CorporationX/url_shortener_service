package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Optional<String> findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash=?";

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        sql,
                        String.class,
                        hash
                )
        );
    }

    @Transactional
    public void save(String hash, String url) {
        String sql = "INSERT INTO url (hash, url) VALUES (?, ?)";
        jdbcTemplate.update(sql, hash, url);
    }

    @Transactional
    public List<String> deleteOldUrl() {
        String sql = """
                DELETE FROM url
                WHERE created_at < NOW() - INTERVAL '1 year'
                RETURNING hash
                """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

}
