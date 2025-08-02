package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers(int n) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, n);
    }

    @Override
    public void saveAll(List<String> hashes) {
        String sql = "INSERT INTO hashes(hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (preparedStatement, hash) -> preparedStatement.setString(1, hash));
    }

    @Override
    @Transactional
    public List<Hash> getHashBatch(int batchSize) {
        String sql = """
            DELETE FROM hashes WHERE hash IN (
                SELECT hash FROM hashes ORDER BY random() LIMIT ?
            )
            RETURNING hash
            """;
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNum) -> new Hash(resultSet.getString("hash")),
                batchSize
        );
    }

}
