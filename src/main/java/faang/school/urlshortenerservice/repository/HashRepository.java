package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(
            "SELECT NEXTVAL('unique_number_seq') FROM GENERATE_SERIES(1, ?)",
            Long.class,
            n
        );
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO hash (hash) VALUES (?)",
            hashes,
            hashes.size(),
            (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashBatch(int n) {
        return jdbcTemplate.query(
            "DELETE FROM hash WHERE hash IN (SELECT hash from hash ORDER BY RANDOM() LIMIT ?) RETURNING hash",
            (rs, rowNum) -> rs.getString("hash"),
            n
        );
    }
}