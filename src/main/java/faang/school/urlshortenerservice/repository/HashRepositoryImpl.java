package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch.size:50}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int count) {
        return jdbcTemplate.query(
                "SELECT nextval('unique_number_seq') AS num FROM generate_series(1, ?)",
                (rs, rowNum) -> rs.getLong("num"),
                count
        );
    }

    @Transactional
    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash, created_at) VALUES (?, NOW())",
                hashes.stream()
                        .map(hash -> new Object[]{hash})
                        .toList()
        );
    }

    @Transactional
    public List<String> getHashBatch() {
        return jdbcTemplate.query(
                "DELETE FROM hash WHERE id IN (SELECT id FROM hash ORDER BY RANDOM() LIMIT ?) RETURNING hash",
                (rs, rowNum) -> rs.getString("hash"),
                batchSize
        );
    }
}
