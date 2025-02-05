package faang.school.urlshortenerservice.repozitory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HashJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HashJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveBatch(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        List<Object[]> batchArgs = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
