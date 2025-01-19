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

    @Value("app.hash.batch-size")
    private Integer BATCH_SIZE;

    public List<Long> getUniqueNumbers(long n) {
        String sql = "select nextval('unique_number_seq') from generate_series(1, ?)";

        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    public void save(List<String> hashes) {
        String sql = "insert into hash (hash) values (?)";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(), (ps, argument) ->
                ps.setString(1, argument)
        );
    }

    public List<String> getHashBatch() {
        String sql = "delete from hash where hash in (select hash from hash limit ?) returning hash";

        return jdbcTemplate.queryForList(sql, String.class, BATCH_SIZE);
    }
}
