package faang.school.urlshortenerservice.repository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash.batch.size}")
    private int batchSize;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(int n) {
        if (n <= 0) {
            return Collections.emptyList();
        }

        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Transactional
    public void save(@NotNull List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES(?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize,
                (ps, hash) -> ps.setString(1, hash));
    }

    @Transactional
    public List<String> getHashBatch() {
        String sql = "DELETE FROM hash WHERE hash IN "
                + "(SELECT hash FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }

}
