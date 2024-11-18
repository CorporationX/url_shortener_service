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

    @Value("${hash.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(long amount) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, amount);
    }

    public void save(List<String> hashes) {
        List<Object[]> mappedList = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();

        jdbcTemplate.batchUpdate("INSERT INTO hash VALUES(?)", mappedList);
    }

    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM hash 
                WHERE hash in (SELECT hash FROM hash LIMIT ?) 
                RETURNING HASH
                """;
        return jdbcTemplate.queryForList(sql, String.class, batchSize);
    }
}
