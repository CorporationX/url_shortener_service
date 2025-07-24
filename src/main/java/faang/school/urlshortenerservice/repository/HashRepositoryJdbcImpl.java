package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryJdbcImpl implements HashRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConstantsProperties constantsProperties;

    @Override
    @Transactional
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Override
    @Transactional
    public boolean save(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        try {
            int[][] updateCountArray = jdbcTemplate.batchUpdate(
                    sql, hashes, hashes.size(), (ps, hash) -> ps.setString(1, hash));
            boolean anyFailed = Arrays.stream(updateCountArray)
                    .flatMapToInt(Arrays::stream)
                    .anyMatch(updateCount -> updateCount == 0);
            return !anyFailed;
        } catch (DataAccessException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public List<String> getHashBatch() {
        String sql = "DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT ?) RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, constantsProperties.getLocalHashCacheButchSize());
    }

    @Override
    public Long countHashes() {
        String sql = "SELECT COUNT(*) FROM hash";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
