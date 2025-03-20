package faang.school.urlshortenerservice.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<String> deleteExpiredUrls() {
    String sql = """
            DELETE FROM url 
            WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING hash
        """;

    return jdbcTemplate.queryForList(sql, String.class);
  }
}