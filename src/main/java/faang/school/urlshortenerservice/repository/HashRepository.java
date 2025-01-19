package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.properties.HashRepositoryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;


import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final HashRepositoryProperties hashRepoProps;

    public List<Long> getUniqueNumbers(int n) {
        String sql = """
            SELECT nextval('unique_number_seq')
            FROM generate_series(1, :n)
        """;
        var params = Collections.singletonMap("n", n);

        return namedJdbcTemplate.queryForList(sql, params, Long.class);
    }

    public void save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (:hash)";

        SqlParameterSource[] batchParams = new SqlParameterSource[hashes.size()];
        for (int i = 0; i < hashes.size(); i++) {
            batchParams[i] = new MapSqlParameterSource("hash", hashes.get(i));
        }

        namedJdbcTemplate.batchUpdate(sql, batchParams);
        log.info("Saved {} hashes in batch", hashes.size());
    }

    public List<String> getHashBatch(int limit) {
        String sql = """
            WITH cte AS (
              SELECT hash
              FROM hash
              ORDER BY random()
              LIMIT :limit
            )
            DELETE FROM hash h
            USING cte
            WHERE h.hash = cte.hash
            RETURNING cte.hash
        """;

        var params = new MapSqlParameterSource("limit", limit);

        return namedJdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getString(1));
    }
}

