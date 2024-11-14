package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.properties.HashProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;
    private final HashProperties hashProperties;

    public List<Long> getUniqueNumbers(int count) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

        return jdbcTemplate.query(sql,
                preparedStatement -> preparedStatement.setInt(1, count),
                (rs, rowNum) -> rs.getLong(1));
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO free_hash_set (hash_value) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (ps, hashValue) -> ps.setString(1, hashValue));

        log.info("Save hashes to database: {}", hashes);
    }

    public List<String> getHashBatch() {
        String sql = """
                 DELETE FROM free_hash_set
                 WHERE hash_value IN (
                     SELECT hash_value FROM free_hash_set
                     LIMIT ?
                 )
                 RETURNING hash_value
                """;
        return jdbcTemplate.queryForList(sql, new Object[]{hashProperties.getHashBatchSize()}, String.class);
    }
}


