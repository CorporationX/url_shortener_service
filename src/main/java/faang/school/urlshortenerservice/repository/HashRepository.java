package faang.school.urlshortenerservice.repository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash.batch.size}")
    private int batchSize;
    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }

        String sql = "SELECT nextval('unique_number_sequence') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    public void save(@NotNull List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES(?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize,
                (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        String sql = "DELETE FROM hash WHERE id IN "
                + "(SELECT id FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
