package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch.size}")
    private int hashBatchSize;

    public List<Long> getUniqueNumbers(int n) {
        return jdbcTemplate.queryForList(
            "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)",
            Long.class,
            n
        );
    }

    public void save(List<String> hashes) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO (hash) VALUES (?)",
            hashes, hashes.size(), (ps, hash) -> ps.setString(1, hash)
        );
    }

    public List<String> getHashBatch() {
        List<String> hashes = jdbcTemplate.queryForList(
            "SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?",
            String.class,
            hashBatchSize
        );

        if (!hashes.isEmpty()) {
            String placeholders = hashes.stream()
                .map(hash -> "?")
                .collect(Collectors.joining(","));

            jdbcTemplate.update("DELETE FROM hash WHERE hash IN (" + placeholders + ")", hashes);
        }
        return hashes;
    }
}