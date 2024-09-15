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

    @Transactional
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES(?)";

        List<Object[]> batchArgs = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Transactional
    public List<String> getHashesAndDelete(int batchSize) {
        String sql = "WITH random_hashes AS (" +
                " SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?) " +
                " DELETE FROM hash WHERE hash IN (SELECT hash FROM random_hashes) " +
                " RETURNING hash";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"), batchSize);
    }

    public Long getActualHashCount() {
        String sql = "select count(*) from hash";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}