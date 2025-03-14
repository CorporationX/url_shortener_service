package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashJdbcRepository implements HashRepository {

    private final JdbcTemplate jdbcTemplate;
    
    @Value("${hash.batch.size}")
    private int batchSize;

    public List<Long> getUniqueNumbers() {
        String sql = "SELECT nextval('unique_numbers_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, batchSize);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash_value) VALUES(?)";
        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        String sql = "DELETE FROM hash WHERE hash_value IN (SELECT hash_value FROM hash LIMIT ?) RETURNING hash_value";
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}