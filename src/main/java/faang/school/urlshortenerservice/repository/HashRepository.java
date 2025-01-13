package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Transactional
@Component
@RequiredArgsConstructor
public class HashRepository {

  private final JdbcTemplate jdbcTemplate;

  public List<Long> getNumbers(int n) {
    return jdbcTemplate.queryForList(
        "SELECT nextval('unique_number_seq') FROM generate_series(1, :n)", Long.class);
  }
  // TODO в принципе все через JdbcTemplate можно, не делать отдельный JPA repo

  //TODO: так понимаю это класс который будет импрелемтить еще и repo interface, в котором запросы будут
  // 1.
  // List<Long> getNumbers(int n) - get from sequence - jdbcTemplate! - будет запрашиваться бином HashGenerator
  // 2.
  // List<Hash> getHashBatch(n) - get from table 'hash':
  // "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :n) RETURNING *"
  // 3.
  // void save(hashes) - SQL or saveAll - будет передаваться сюда на сохранение бином HashGenerator
  // @Transactional do not forget
}
