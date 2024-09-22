package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(@Param("amount") int amount) {
        return jdbcTemplate.query("select nextval('unique_number_seq') as genNum from generate_series(1,?)"
                , (rs, rowNum) -> rs.getLong("genNum"), amount);
    }

    public int[] saveHashes(List<String> hashes) {
        return jdbcTemplate.batchUpdate(
                "insert into hash(hash) values (?) on conflict (hash) do nothing "
                , new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, hashes.get(i));
                    }

                    public int getBatchSize() {
                        return hashes.size();
                    }
                });
    }

    public List<String> getAndDeleteHashes(int amount) {
        return jdbcTemplate.query(
                "delete from hash where id in " +
                        "( select hash.id from hash order by random() limit ?)" +
                        " returning hash"
                , (rs, rowNum) -> rs.getString("hash")
                , amount);
    }
}
