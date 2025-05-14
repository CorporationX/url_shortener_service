package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> getUniqueNumbers(int count) {
        if (count <= 0) {
            log.error("Count must be positive. Count = {}", count);
            throw new IllegalArgumentException("Count must be positive");
        }

        try {
            String sql = """
                    SELECT nextval('unique_number_seq') AS uniq_number
                    FROM generate_series(1, :count)
                    """;
            MapSqlParameterSource params = new MapSqlParameterSource("count", count);
            return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong("uniq_number"));
        } catch (Exception e) {
            log.error("Failed to retrieve unique numbers from sequence, count: {}", count, e);
            throw new IllegalStateException("Unable to retrieve unique numbers", e);
        }
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            log.debug("No hashes to save");
            return;
        }

        try {
            String sql = "INSERT INTO hash (hash) VALUES (:hash) ON CONFLICT (hash) DO NOTHING";
            MapSqlParameterSource[] params = hashes.stream()
                    .map(hash -> new MapSqlParameterSource("hash", hash))
                    .toArray(MapSqlParameterSource[]::new);
            int[] results = jdbcTemplate.batchUpdate(sql, params);
            int inserted = 0;
            for (int result : results) {
                inserted += result;
            }
            log.info("Inserted {} hashes into hash table", inserted);
        } catch (Exception e) {
            log.error("Failed to save hashes, size: {}", hashes.size(), e);
            throw new IllegalStateException("Unable to save hashes", e);
        }
    }

    @Transactional
    public List<String> getHashes(int count) {
        if (count <= 0) {
            log.error("Count must be positive: {}", count);
            throw new IllegalArgumentException("Count must be positive");
        }

        try {
            String sql = """
                    WITH selected_hashes AS (
                        SELECT hash
                        FROM hash
                        ORDER BY random()
                        LIMIT :count
                        FOR UPDATE SKIP LOCKED
                    )
                    DELETE FROM hash
                    WHERE hash IN (SELECT hash FROM selected_hashes)
                    RETURNING hash
                    """;
            MapSqlParameterSource params = new MapSqlParameterSource("count", count);
            List<String> hashes = jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getString("hash"));
            if (hashes.isEmpty()) {
                log.warn("No hashes available in hash table");
            } else {
                log.debug("Retrieved {} hashes from hash table", hashes.size());
            }
            return hashes;
        } catch (Exception e) {
            log.error("Failed to retrieve hashes, count: {}", count, e);
            throw new IllegalStateException("Unable to retrieve hashes", e);
        }
    }

    public long getCountOfHashes() {
        try {
            String sql = "SELECT count(*) FROM hash";
            Long count = jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Failed to retrieve hash count", e);
            throw new IllegalStateException("Unable to retrieve hash count", e);
        }
    }
}
