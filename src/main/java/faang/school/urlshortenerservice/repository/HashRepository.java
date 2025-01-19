package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
@RequiredArgsConstructor
public class HashRepository {

  @Value("${generator.hashes.add-to-local-cache}")
  private int n;

  private final JdbcTemplate jdbcTemplate;

  public List<Long> getUniqueNumbers(int n) {

    return jdbcTemplate.queryForList(
        "SELECT nextval('unique_number_hash') FROM generate_series(1, ?)", Long.class, n);
  }

  public void save(List<String> hashes) {
    String sql = "INSERT INTO hash(hash) VALUES(?)";
    jdbcTemplate.batchUpdate(sql, hashes, n, (ps, hash) -> {
      ps.setString(1, hash);
    });
  }

  public List<String> takeHashBatch() {
    return jdbcTemplate.queryForList(
        "DELETE FROM hash h WHERE h.hash IN (SELECT h.hash FROM hash h ORDER BY RANDOM() LIMIT ?) RETURNING *",
        String.class, n
    );
  }
}
