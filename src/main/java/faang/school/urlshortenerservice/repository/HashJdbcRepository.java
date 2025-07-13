package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_HASH_SQL = "INSERT INTO hash (hash) VALUES (?)";

    @Transactional
    public void saveBatch(List<String> hashes) {
        List<Object[]> batchArgs = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();
        jdbcTemplate.batchUpdate(INSERT_HASH_SQL, batchArgs);
    }
}
