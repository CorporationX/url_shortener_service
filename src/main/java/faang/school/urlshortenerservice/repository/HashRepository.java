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
        String query = "select nextval('unique_number_seq') as unique_numbers from generate_series(1, :quantity)";
        MapSqlParameterSource params = new MapSqlParameterSource("quantity", quantity);
        return jdbcTemplate.queryForList(query, params, Long.class);
    }

    public void batchSave(List<String> hashes) {
        String query = "insert into hash (hash) values(:hash)";
        MapSqlParameterSource[] params = new MapSqlParameterSource[hashes.size()];
        for (int i = 0; i < hashes.size(); i++) {
            params[i] = new MapSqlParameterSource("hash", hashes.get(i));
        }
        jdbcTemplate.batchUpdate(query, params);
    }

    public List<String> getHashBatch(int batchSize) {
        String query = """
                with deleted_rows as (
                    select * from hash
                    where true
                    limit :batch_size
                    for update skip locked
                )
                delete from hash
                using deleted_rows
                where hash.hash = deleted_rows.hash
                returning hash.hash
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("batch_size", batchSize);
        return jdbcTemplate.queryForList(query, params, String.class);
    }
}