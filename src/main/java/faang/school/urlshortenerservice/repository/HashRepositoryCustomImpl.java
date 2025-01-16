package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.hashconfig.HashConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepositoryCustomImpl implements HashRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    private final HashConfig hashConfig;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers(int count) {
        int batchSize = (count > 0) ? count : hashConfig.getBatchSize();
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.query(sql, ps -> ps.setInt(1, batchSize), (rs, rowNum) -> rs.getLong(1));
    }

    @Override
    public void saveHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            log.warn("No hashes provided to save.");
            return;
        }

        String sql = "INSERT INTO hash (hash) VALUES (:hash)";
        Map<String, String>[] batchParams = hashes.stream()
                .map(hash -> {
                    Map<String, String> param = new HashMap<>();
                    param.put("hash", hash);
                    return param;
                })
                .toArray(HashMap[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, batchParams);
        log.info("Saved {} hashes to the database", hashes.size());
    }

    @Override
    public List<String> getHashBatch(int count) {
        int batchSize = (count > 0) ? count : hashConfig.getBatchSize();
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.query(sql, ps -> ps.setInt(1, batchSize), (rs, rowNum) -> rs.getString(1));
    }
}

