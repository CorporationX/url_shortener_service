package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomHashRepositoryImpl implements CustomHashRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private static final String SQL_GET_NEXT_UNIQUE_HASH_NUMBERS =
            "SELECT nextval('unique_hash_number_seq') FROM generate_series(1, :maxRange)";

    private static final String SAVE_BATCH = """
            INSERT INTO hash (hash) VALUES (:hash)
            """;


    @Override
    public List<Long> getNextRange(int maxRange) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("maxRange", maxRange);
        List<Long> uniqueHashNumbers = namedJdbcTemplate.queryForList(SQL_GET_NEXT_UNIQUE_HASH_NUMBERS, params, Long.class);
        return uniqueHashNumbers;
    }

    @Override
    public int[] saveAllBatch(List<Hash> hosts) {
        MapSqlParameterSource[] args = hosts.stream()
                .map(host -> new MapSqlParameterSource()
                        .addValue("hash", host.getHash()))
                .toArray(MapSqlParameterSource[]::new);
        return namedJdbcTemplate.batchUpdate(SAVE_BATCH, args);
    }
}
