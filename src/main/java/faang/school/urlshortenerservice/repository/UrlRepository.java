package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> removeOldLinks(){
        String sql = """
                DELETE FROM url
                WHERE created_at < ?
                RETURNING hash
                """;
       return jdbcTemplate.queryForList(sql, String.class, LocalDateTime.now().minusYears(1));
    }
}
