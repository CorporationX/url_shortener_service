package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.DatabaseAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static faang.school.urlshortenerservice.exception.ErrorMessages.DATABASE_ACCESS_ERROR;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${shortener.hash.reserve.batch-size}")
    private int batchSize;

    public List<Long> getUniqueNumbers(int n) {
        try {
            return jdbcTemplate.queryForList("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
                    Long.class, n);
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(DATABASE_ACCESS_ERROR, e);
        }

    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)", hashes, 100, (ps, hash) -> ps.setString(1, hash));
    }

    public List<String> getHashBatch() {
        return jdbcTemplate.queryForList("""
                DELETE FROM hash 
                WHERE ctid IN (
                    SELECT ctid FROM hash LIMIT ?
                ) 
                RETURNING hash
                """, String.class, batchSize);
    }

    public void saveAll(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)", hashes, 100, (ps, hash) -> ps.setString(1, hash));
    }
}
