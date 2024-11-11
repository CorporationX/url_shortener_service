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

    @Value("${hash.batch-size.get}")
    private int getBatchSize;

    public List<Long> getUniqueNumbers(int amount) {
        String sql = """
                select nextval('unique_number_seq')
                from generate_series(1, ?)
                """;
        return jdbcTemplate.queryForList(sql, Long.class, amount);
    }

    public void save(List<String> hashes) {
        if (hashes.isEmpty()) {
            return;
        }
        String sql = """
                insert into hash (hash)
                values (?)
                """;
        List<Object[]> mappedList = hashes.stream()
                .map(hash -> new Object[]{hash})
                .toList();
        jdbcTemplate.batchUpdate(sql, mappedList);
    }

    public List<String> getHashBatch() {
        String sql = """
                delete from hash
                where hash in (select hash from hash limit ?)
                returning hash
                """;
        return jdbcTemplate.queryForList(sql, String.class, getBatchSize);
    }
}
