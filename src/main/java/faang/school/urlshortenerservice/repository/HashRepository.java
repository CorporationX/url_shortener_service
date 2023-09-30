package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.application.sequence.batch-size}")
    private int batchSize;


    public List<Long> getUniqueNumbers() {
        return jdbcTemplate.queryForList(
                "SELECT NEXTVAL('unique_number_seq') FROM generate_series(1, ?)", Long.class, batchSize);
    }

    @Modifying
    public void save(List<String> hashes) {
        List<List<String>> partitions = ListUtils.partition(hashes, batchSize);
        partitions.forEach(partition -> jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setString(1, partition.get(i));
                    }

                    public int getBatchSize() {
                        return partition.size();
                    }
                }));
    }

    public List<String> getHashBatch(int n) {
        return jdbcTemplate.queryForList(
                "DELETE FROM hash WHERE ctid IN (SELECT ctid FROM hash TABLESAMPLE BERNOULLI(1) LIMIT ?) RETURNING hash",
                String.class, n);
    }
}
