package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final JdbcTemplate jdbcTemplate;
    private static final int batchCounter = 5;

    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            @Override
            public int getBatchSize() {
                return batchCounter;
            }
        });
    }
}
