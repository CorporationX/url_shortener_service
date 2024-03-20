package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${batch.sublists}")
    private int HashBatchSize;

    public List<Long> getUniqueNumbers(int numberOfValue) {
        return jdbcTemplate.queryForList("SELECT nextval('unique_number_seq') FROM generate_series(1,?)",
                new Object[]{numberOfValue}, Long.class);
    }

    public void save(List<String> hashes) {
        List<List<String>> hashBathes = ListUtils.partition(hashes, HashBatchSize);
        for (List<String> hashBatch : hashBathes) {
            String sql = "INSERT INTO hash (hash) VALUES (?)";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, hashBatch.get(i));
                }

                @Override
                public int getBatchSize() {
                    return hashBatch.size();
                }
            });
        }
    }

    @Transactional
    public List<String> getHashBatch(int numberOfValue) {
        String sql = "WITH deleted AS (DELETE FROM hash RETURNING hash) SELECT hash FROM deleted LIMIT ?";
        return jdbcTemplate.queryForList(sql, String.class, numberOfValue);
    }
}
