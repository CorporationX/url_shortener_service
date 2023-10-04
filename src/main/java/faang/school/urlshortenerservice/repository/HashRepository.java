package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> generatedValues(int size) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        List<Long> generatedNumbers = jdbcTemplate.queryForList(sql, Long.class, size);
        return generatedNumbers;
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes, hashes.size(),
                (ps, hash) -> ps.setString(1, hash));
    }

    @Transactional
    public List<String> getHashBatch(int size) {
        String deleteSql = "DELETE FROM hash " +
                "WHERE id IN (" +
                "    SELECT id " +
                "    FROM hash " +
                "    LIMIT ?" +
                ") " +
                "RETURNING hash";

        return jdbcTemplate.queryForList(deleteSql, String.class, size);
    }

    @Transactional
    public List<String> findAndDeleteOldHashes(LocalDateTime time) {
        String deleteSql = "DELETE FROM url WHERE created_at <= ? RETURNING hash";
        List<String> deletedHashes = jdbcTemplate.queryForList(deleteSql, String.class, time);
        return deletedHashes;
    }

    @Transactional
    public String saveUrlAndHash(String url, String hash) {
        String sql = "INSERT INTO url(url, hash, created_at) VALUES (?, ?, ?) RETURNING hash";
        return jdbcTemplate.queryForObject(sql, String.class, url, hash, LocalDateTime.now());
    }

    @Transactional
    public Optional<String> getOriginalUrl(String shortUrl) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, String.class, shortUrl));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
