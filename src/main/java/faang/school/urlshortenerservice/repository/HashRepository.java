package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch.size.get}")
    private int getBatchSize;
    @Value("${hash.batch.size.insert}")
    private int insertBatchSize;

    public List<Long> getUniqueNumbers(int amount) {
        String sql = """
                    SELECT nextval('unique_numbers_seq')
                    FROM generate_series(1, ?)
                    """;
        return jdbcTemplate.queryForList(sql, Long.class, amount);
    }

    public void save(List<String> hashes) {
        String sql = """
                    INSERT INTO hash (hash) VALUES (?)
                    """;
        for (int i = 0; i < hashes.size(); i += insertBatchSize) {
            List<String> batchList = hashes.subList(i, Math.min(i + insertBatchSize, hashes.size()));
            jdbcTemplate.batchUpdate(sql, batchList, batchList.size(),
                    (ps, hash) -> ps.setString(1, hash)
            );
        }
    }

    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash
                    FROM hash
                    ORDER BY RANDOM()
                    LIMIT ?
                )
                RETURNING hash
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"), getBatchSize);
    }
}
