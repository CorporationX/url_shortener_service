package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcHashRepository implements HashRepository{
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        String sql = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, ?)
                """;
        return jdbcTemplate.queryForList(sql, new Object[]{n}, Long.class);
    }

    @Override
    @Transactional
    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hashes (hash) VALUES (?)";

        for (int i = 0; i < hashes.size(); i += batchSize) {
            int end = Math.min(i + batchSize, hashes.size());
            List<Object[]> batchArgs = new ArrayList<>();

            for (String h : hashes.subList(i, end)) {
                batchArgs.add(new Object[]{h});
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Transactional
    @Override
    public List<String> getHashBatch() {
        String sql = """
                WITH picked AS (
                SELECT hash FROM hashes ORDER BY random() LIMIT ?
                )
                DELETE FROM hashes USING picked WHERE hashes.hash = picked.hash
                RETURNING hashes.hash
                """;
        return jdbcTemplate.queryForList(sql, new Object[]{batchSize}, String.class);
    }
}