package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.max_batch_size}")
    private int maxBatchSize;

    public List<Long> getUniqueNumbers(int count) {
        String sql = "select nextval('unique_number_seq') from generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }

    public void saveBatch(List<String> hashes) {
        if (hashes.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate("insert into hash (hash) values (?)", new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, hashes.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return hashes.size();
                    }
                }
        );
    }

    @Override
    public List<String> getHashBatch() {
        String sql = "delete from hash where hash in (select hash from hash limit ?) returning hash";
        return jdbcTemplate.queryForList(sql, String.class, maxBatchSize);
    }
}
