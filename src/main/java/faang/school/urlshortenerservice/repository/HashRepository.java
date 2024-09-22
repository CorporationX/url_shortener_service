package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(int numberUniqueNumbers) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, numberUniqueNumbers);
    }

    @Transactional
    public List<String> getAndDeleteHashBatch(int numberRandomHashes) {
        String sql = """
                DELETE FROM hash WHERE hash IN (
                SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?
                )
                RETURNING *
                """;
        return jdbcTemplate.queryForList(sql, String.class, numberRandomHashes);
    }

    @Transactional
    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        List<Object[]> batchHash = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();
        jdbcTemplate.batchUpdate(sql, batchHash);
    }
}