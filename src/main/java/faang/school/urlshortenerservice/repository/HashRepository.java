package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size.insert}")
    private int batchSizeInsert;
    @Value("${hash.batch-size.get}")
    private int batchSizeGet;

    public List<Long> getUniqueNumbers(int amount) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

        return jdbcTemplate.queryForList(sql, Long.class, amount);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";

        for (int i = 0; i < hashes.size(); i += batchSizeInsert) {
            List<String> batchList = hashes.subList(i, Math.min(i + batchSizeInsert, hashes.size()));
            jdbcTemplate.batchUpdate(sql, batchList, batchList.size(),
                    (s, hash) -> s.setString(1, hash)
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
        return jdbcTemplate.query(sql, (res, rowNum) -> res.getString("hash"), batchSizeGet);
    }
}
