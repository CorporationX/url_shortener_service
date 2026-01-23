package faang.school.urlshortenerservice.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public List<String> deleteOldUrlsAndGetHashes(int deleteOlderThanDays) {
        String sql = """
                DELETE FROM url
                WHERE created_at < NOW() - INTERVAL '1 day' * ?
                RETURNING hash""";

        List<String> deletedHashes = jdbcTemplate.queryForList(sql, String.class, deleteOlderThanDays);
        entityManager.flush();
        entityManager.clear();
        return deletedHashes;
    }
}
