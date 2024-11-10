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

    @Value("${hash.batch-size.insert}")
    private int insertBatchSize;

    @Value("${hash.batch-size.get}")
    private int getBatchSize;

    public List<Long> getUniqueNumbers(int n) {
        String sql = """
                select nextval('unique_number_seq')
                from generate_series(1, ?)
                """;
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    public void save(List<String> hashes) {
        String sql = """
                insert into hash (hash)
                values (?)
                """;
        for (int i = 0; i < hashes.size(); i += insertBatchSize) {
            List<String> batchList = hashes.subList(i, Math.min(i + insertBatchSize, hashes.size()));
            jdbcTemplate.batchUpdate(sql, batchList, batchList.size(),
                    (ps, hash) -> ps.setString(1, hash)
            );
        }
    }

    public List<String> getHashBatch() {
        String sql = """
                delete from hash
                where hash in (select hash from hash limit ?)
                returning hash
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"), getBatchSize);
    }
}
