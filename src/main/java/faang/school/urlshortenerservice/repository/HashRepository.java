package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${hashRepository.batchSize}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(), (ps, argument) -> {
            ps.setString(1, argument);
        });
        log.info("Saved {} hashes in batch.", hashes.size());
    }

    public List<String> getHashBatch() {
        String sql = "WITH deleted AS ( " +
                "  DELETE FROM hash " +
                "  WHERE hash IN (" +
                "    SELECT hash FROM hash ORDER BY random() LIMIT ? " +
                "  ) " +
                "  RETURNING hash) " +
                "SELECT hash FROM deleted";

        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
