package faang.school.urlshortenerservice.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UrlJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<String> deleteOldUrlsAndReturnHashes() {
        return jdbcTemplate.queryForList(
                """
                DELETE FROM url
                WHERE created_at < NOW() - INTERVAL '1 year'
                RETURNING hash
                """,
                String.class
        );
    }
}

