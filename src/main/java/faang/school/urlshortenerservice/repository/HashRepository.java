package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
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

    public List<Long> getUniqueNumbers(long uniqueNumbers) {
        String sql = "SELECT nextval('unique_numbers_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, uniqueNumbers);
    }

    public void save(List<Hash> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash VALUES (?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i).getHash());
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    public List<Hash> getHashBatch(long uniqueNumbers) {
       return jdbcTemplate.queryForList(
               "DELETE FROM hash where ctid IN (SELECT ctid FROM hash LIMIT ?) RETURNING *",
               Hash.class, uniqueNumbers);
    }
}
