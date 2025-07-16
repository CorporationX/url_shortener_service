package faang.school.urlshortenerservice.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size:100}")
    private int batchSize;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Override
    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, batchSize,
                (preparedStatement, hash) -> preparedStatement.setString(1, hash));
    }

    @Override
    @Transactional
    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM hash WHERE hash IN (
                    SELECT hash FROM hash ORDER BY random() LIMIT ?
                )
                RETURNING hash
                """;
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> resultSet.getString("hash"),
                batchSize
        );
    }
}
