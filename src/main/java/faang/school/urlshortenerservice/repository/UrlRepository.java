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

    public List<String> deleteOldEntriesAndReturnHashes() {
        String sql = """
                delete from url
                where created_at < ?
                returning hash
                """;
        LocalDateTime dateToRemove = LocalDateTime.now().minusYears(1);
        return jdbcTemplate.queryForList(sql, String.class, dateToRemove);
    }
}
