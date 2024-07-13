package faang.school.urlshortenerservice.repository;

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
public class HashRepository {

     private final JdbcTemplate jdbcTemplate;

     @Value("${hash.batch-size.size}")
     private long batchSize;

     public List<Long> getUniqueNumbers(long n) {
          String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

          return jdbcTemplate.query(sql,
                  new Object[]{n},
                  ((rs, rowNum) -> rs.getLong(1)));
     }

     public void save(List<String> hashes) {
          String sql = "INSERT INTO hash (hash) VALUES (?)";
          jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
               @Override
               public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, hashes.get(i));
               }

               @Override
               public int getBatchSize() {
                    return hashes.size();
               }
          });
     }

     public List<String> getHashBatch() {
          String sql = "DELETE FROM hash WHERE id IN (SELECT id FROM hash LIMIT ?) RETURNING hash";
          return jdbcTemplate.queryForList(sql, String.class, batchSize);
     }

     public Integer getHashesCount() {
          String sql = "SELECT COUNT(*) from hash";
          return jdbcTemplate.queryForObject(sql, Integer.class);
     }
}
