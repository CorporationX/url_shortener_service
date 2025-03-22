package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {

  private final JdbcTemplate jdbcTemplate;
  private final UrlShortenerProperties properties;

  public List<Long> getUniqueNumbers() {
    String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
    return jdbcTemplate.queryForList(sql, Long.class, properties.getUniqueNumbersCount());
  }

  public void save(List<String> hashes) {
    String sql = "INSERT INTO hash (hash) VALUES (?)";
    jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
        (ps, hash) -> ps.setString(1, hash));
  }

  public List<String> findAndDelete(Long size) {
    String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING hash";
    return jdbcTemplate.queryForList(sql, String.class, size);
  }
}