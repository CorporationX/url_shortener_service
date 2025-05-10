package faang.school.urlshortenerservice.repository.implementations;

import faang.school.urlshortenerservice.config.app.HashConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    private final HashConfig hashConfig;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.query(
                "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                (rs, rowNum) -> rs.getLong(1),
                n
        );
    }

    @Override
    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
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
        int batchSize = hashConfig.getBatchSize();
        return jdbcTemplate.query(
                """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash
                    FROM hash
                    ORDER BY RANDOM()
                    LIMIT ?
                )
                RETURNING hash
                """,
                (rs, rowNum) -> rs.getString("hash"),
                batchSize
        );
    }
}
