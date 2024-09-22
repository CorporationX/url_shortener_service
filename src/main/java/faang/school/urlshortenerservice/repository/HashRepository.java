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

    @Value("${spring.batch.size}")
    private int batchSize;
    private String sql;

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash));
    }

    public List<Long> getUniqueNumbers() {
        sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

        return jdbcTemplate.query(
                sql,
                (result, rowNum) -> result.getLong(1),
                batchSize
        );
    }

    public List<String> getHashBatch() {
        sql = "DELETE FROM hash USING ("
                + "    SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?"
                + ") AS sub WHERE hash.hash = sub.hash RETURNING hash.hash";

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> resultSet.getString("hash"),
                batchSize
        );
    }

    public Long getCount() {
        sql = "SELECT COUNT(*) FROM hash";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
