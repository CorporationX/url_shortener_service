package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int quantity) {
        String query = "SELECT NEXTVAL('unique_number_seq') AS unique_numbers FROM generate_series(1, :quantity)";
        MapSqlParameterSource params = new MapSqlParameterSource("quantity", quantity);
        return jdbcTemplate.queryForList(query, params, Long.class);
    }

    public void batchSave(List<String> hashes) {
        String query = "INSERT INTO hash (hash) VALUES (:hash)";
        MapSqlParameterSource[] params = new MapSqlParameterSource[hashes.size()];
        for (int i = 0; i < hashes.size(); i++) {
            params[i] = new MapSqlParameterSource("hash", hashes.get(i));
        }
        jdbcTemplate.batchUpdate(query, params);
    }

    public List<String> getHashBatch(int batchSize) {
        String query = """
                WITH deleted_rows AS (
                    SELECT * FROM hash
                    WHERE true
                    LIMIT :batch_size
                    FOR UPDATE SKIP LOCKED
                )
                DELETE FROM hash
                USING deleted_rows
                WHERE hash.hash = deleted_rows.hash
                RETURNING hash.hash
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("batch_size", batchSize);
        return jdbcTemplate.queryForList(query, params, String.class);
    }
}