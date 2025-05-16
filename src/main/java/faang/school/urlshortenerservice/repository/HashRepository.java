package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${cache.capacity}")
    private Integer cacheCapacity;

    public List<Long> getUniqueNumbers() {
        return jdbcTemplate.queryForList(
                String.format("SELECT nextval('unique_number_seq') FROM generate_series(1, %d)", cacheCapacity),
                Long.class
        );
    }

    @Transactional
    public void saveBatch(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(
                sql,
                hashes,
                hashes.size(),
                ((ps, argument) -> ps.setString(1, argument))
        );
    }

    @Transactional
    public List<String> getAndDeleteHashBatch(int uniqueNumbersBatch) {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.query(
                sql,
                ps -> ps.setInt(1, uniqueNumbersBatch),
                (rs, rowNum) -> rs.getString("hash")
        );
    }
}
