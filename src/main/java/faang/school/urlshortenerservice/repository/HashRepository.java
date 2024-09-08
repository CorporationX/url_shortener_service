package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(@Param("amount") @Value("${hashes.amount}") int amount) {
        return jdbcTemplate.query("select nextval('unique_number_seq') as genNum from generate_series(1,:amount)"
                , (rs, rowNum) -> rs.getLong("genNum"));
    }

    public int[] saveHashes(List<String> hashes) {
        return jdbcTemplate.batchUpdate("insert into hash values (?) on conflict do nothing", new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            public int getBatchSize() {
                return hashes.size();
            }
        });
    }
}
