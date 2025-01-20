package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> deleteOlderThanOneYear() {
        String sql = """
            DELETE FROM url
            WHERE created_at < (CURRENT_TIMESTAMP - INTERVAL '1 year')
            RETURNING hash
        """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("hash")
        );
    }
}

