package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<String> clearOldHashes(int daysAgo) {
        return jdbcTemplate.queryForList(
                "DELETE FROM url WHERE created_at < NOW() - MAKE_INTERVAL(days => ?) RETURNING hash",
                String.class,
                daysAgo
        );
    }

    public Optional<String> getByHash(String hash) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT url FROM url WHERE hash = ?",
                            String.class,
                            hash
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
