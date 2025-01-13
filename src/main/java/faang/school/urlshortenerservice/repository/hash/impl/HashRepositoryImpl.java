package faang.school.urlshortenerservice.repository.hash.impl;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int batchSize) {
        String query = String.format(
                "SELECT nextval('unique_numbers_for_hashes') FROM generate_series(1, %d)", batchSize
        );
        return jdbcTemplate.query(query, (rs, rowNum) -> rs.getLong(1));
    }

    public void saveHashes(List<String> hashes) {
        String query = "INSERT INTO hash (hash) VALUES " +
                String.join(", ", Collections.nCopies(hashes.size(), "(?)"));

        jdbcTemplate.batchUpdate(query, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashBatch(int batchSize) {
        String deleteQuery = "DELETE FROM hash WHERE hash IN (" +
                "SELECT hash FROM hash LIMIT ? FOR UPDATE" +
                ") RETURNING hash";

        return jdbcTemplate.queryForList(deleteQuery, String.class, batchSize);
    }
}
