package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryJDBCImpl implements HashRepositoryJDBC {
    @Value("${hash.batch.size}")
    private int batchSize;

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(), (ps, hash) -> {
            ps.setString(1, hash);
        });
    }

    @Override
    @Transactional
    public List<String> getHashBatch() {
        String sql = "WITH deleted AS " +
                "(DELETE FROM hash WHERE hash IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :n) " +
                "RETURNING hash) SELECT hash FROM deleted";

        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
