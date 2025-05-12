package faang.school.urlshortenerservice.repository.implementations;

import faang.school.urlshortenerservice.config.app.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final HashGeneratorConfig hashGeneratorConfig;

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
        int batchSize = hashGeneratorConfig.getBatchSize();
        log.info("getHashBatch called with batchSize: {}", batchSize);
        return jdbcTemplate.query(
                "DELETE FROM hash WHERE hash IN (" +
                        "SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?)" +
                        " RETURNING hash",
                (rs, rowNum) -> {
                    log.info("Mapping row: {}", rs.getString("hash"));
                    return rs.getString("hash");
                },
                batchSize
        );
    }

    /*@Override
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
    }*/
}
