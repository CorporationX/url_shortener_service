package faang.school.urlshortenerservice.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final int batchSize;

    public HashRepository(JdbcTemplate jdbcTemplate,
                          @Value("${url.hash.batch-size:100}") int batchSize) {
        this.jdbcTemplate = jdbcTemplate;
        this.batchSize = batchSize;
    }

    @Transactional
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Transactional
    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize,
                (ps, hash) -> ps.setString(1, hash));
    }

    @Transactional
    public List<String> getHashBatch() {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY random() LIMIT ?) RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
