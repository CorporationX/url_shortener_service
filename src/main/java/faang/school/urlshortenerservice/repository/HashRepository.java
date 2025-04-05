package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public int saveAll(List<String> hashes) {
        if (hashes.isEmpty()) {
            return 0;
        }

        String sqlInsert = "INSERT INTO hash (hash) VALUES (:hashes) ON CONFLICT DO NOTHING";
        SqlParameterSource[] batchParams = hashes.stream()
                .map(hash -> new MapSqlParameterSource("hashes", hash))
                .toArray(SqlParameterSource[]::new);

        return Arrays.stream(namedParameterJdbcTemplate.batchUpdate(sqlInsert, batchParams)).sum();
    }

    @Transactional
    public List<String> getHashBatch(int numbers) {
        if (numbers <= 0) {
            return List.of();
        }

        String sql = """
                DELETE FROM hash
                WHERE hash
                IN (SELECT hash FROM hash ORDER BY RANDOM() LIMIT :numbers FOR UPDATE SKIP LOCKED)
                RETURNING hash;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("numbers", numbers);

        return namedParameterJdbcTemplate.query(sql, params,
                (rs, rowNum) -> rs.getString("hash")
        );
    }

    @Transactional
    public List<Long> getUniqueNumbers(int numbers) {
        String sql = """
                SELECT nextval('unique_hash_number_seq') AS unique_number FROM generate_series(1, :numbers)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("numbers", numbers);

        return namedParameterJdbcTemplate.query(sql, params,
                (rs, rowNum) -> rs.getLong("unique_number"));
    }

    @Transactional(readOnly = true)
    public long count() {
        String sql = "SELECT count(*) FROM hash";
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class))
                .orElse(0L);
    }
}
