package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UrlShortenerConfig urlShortenerConfig;

    public List<Long> getUniqueNumbers(int uniqueNumbersCount) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, uniqueNumbersCount);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash(hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql,
                hashes,
                urlShortenerConfig.getInsertBatchSize(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashBatch() {
        String sql = """
                DELETE FROM hash
                 WHERE hash IN (SELECT hash
                                  FROM hash
                                 ORDER BY random() LIMIT ?)
                RETURNING hash
                """;
        return jdbcTemplate.query(sql,
                (rs, rownum) -> rs.getString("hash"),
                urlShortenerConfig.getDeleteBatchSize());
    }
}
