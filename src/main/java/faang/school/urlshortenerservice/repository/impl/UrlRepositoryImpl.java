package faang.school.urlshortenerservice.repository.impl;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<String> deleteExpiredUrls() {
    String sql = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING hash
        """;

    return jdbcTemplate.queryForList(sql, String.class);
  }

  public void saveUrl(Url url) {
    String sql = "INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, url.getHash(), url.getUrl(), url.getCreatedAt());
  }

  public Optional<String> findUrlByHash(String hash) {
    String sql = "SELECT url FROM url WHERE hash = ?";
    return jdbcTemplate.query(sql,
        rs -> rs.next() ? Optional.of(rs.getString("url")) : Optional.empty(), hash);
  }
}