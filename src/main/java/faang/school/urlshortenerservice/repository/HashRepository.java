package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(int count) {
        String sql = """
                SELECT nextval('unique_number_seq') AS uniq_number FROM generate_series(1, :count)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("count", count);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong("uniq_number"));
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (:hashes)";
        SqlParameterSource[] sqlParameterSources = hashes.stream()
                .map(hash -> new MapSqlParameterSource("hashes", hash))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, sqlParameterSources);
    }

    @Transactional
    public List<String> getHashes(int count) {
        String sql = """
                WITH random_hashes AS (
                    SELECT hash
                    FROM hash
                    ORDER BY RANDOM() LIMIT :count FOR UPDATE SKIP LOCKED
                )
                DELETE FROM hash
                WHERE hash IN (SELECT hash FROM random_hashes)
                RETURNING hash;
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("count", count);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getString("hash"));
    }

    public Long getCountOfHashes() {
        String sql = "SELECT count(*) FROM hash";
        return jdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
    }
}
