package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Setter
    @Value("${hash.repository.batch-size}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        String query = "SELECT nextval('num_sequence') FROM generate_series(1, ?);";
        return jdbcTemplate.queryForList(query, Long.class, n);
    }

    @Override
    @Transactional
    public void save(List<String> batch) {
        String query = "INSERT INTO hash (hash) VALUES (?);";
        jdbcTemplate.batchUpdate(query, batch, batch.size(), (PreparedStatement ps, String hash) ->
            ps.setString(1, hash));
    }

    @Override
    public List<String> getHashBatch() {
        String query =
                """
                DELETE FROM hash
                WHERE hash IN (
                    SELECT hash FROM hash
                    ORDER BY RANDOM()
                    LIMIT ?)
                RETURNING hash;
                """;
        return jdbcTemplate.queryForList(query, String.class, batchSize);
    }
}
