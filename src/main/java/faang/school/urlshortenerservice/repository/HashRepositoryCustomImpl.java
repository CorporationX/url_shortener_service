package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryCustomImpl implements HashRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    public void saveBatch(List<String> hashes) {
        List<Object[]> batchHashes = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, batchHashes);
    }
}
