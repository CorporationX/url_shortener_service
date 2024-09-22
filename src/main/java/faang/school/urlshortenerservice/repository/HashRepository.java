package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.QueryException;
import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {

    private final JdbcTemplate jdbcTemplate;
    private final HashRepositoryJpa hashRepositoryJpa;

    private final String INSERT_QUERY= "INSERT INTO hash (hash) VALUES (?)";

    @Value("${spring.jpa.properties.hibernate.jdbc.batch-size}")
    private int batchSize;

    public long getSize() {
        return hashRepositoryJpa.count();
    }

    public List<Long> getUniqueNumbers(int count) {
        return hashRepositoryJpa.getUniqueNumbers(count);
    }

    public List<Hash> getHashes(int count) {
        return hashRepositoryJpa.getHashBatch(count);
    }

    public void save(List<String> hashes) {
        try {
            jdbcTemplate.batchUpdate(INSERT_QUERY, hashes, batchSize, (ps, argument) -> {
                ps.setString(1, argument);
            });
        } catch (DataAccessException e) {
            log.error("Error during executing query batch update: {}", e.getMessage());
            throw new QueryException(e.getMessage());
        }
    }
}
