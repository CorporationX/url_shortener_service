package faang.school.urlshortenerservice.repository;

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

    public List<Long> getUniqueNumbers(int count) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";

        return jdbcTemplate.query(sql,
                preparedStatement -> preparedStatement.setInt(1, count),
                (rs, rowNum) -> rs.getLong(1));
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO generated_urls (hash) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash));

        log.info("Save hashes to database: size = {}", hashes.size());
    }

    public List<String> getHashBatch(int batchSize) {
        String sql = """
                 DELETE FROM generated_urls
                 WHERE hash IN (
                     SELECT hash FROM generated_urls
                     LIMIT ?
                 )
                 RETURNING hash
                """;
        return jdbcTemplate.queryForList(sql, new Object[]{batchSize}, String.class);
    }
}
