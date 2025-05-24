package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.HashNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String ERROR_HASH_DOES_NOT_EXIST = "Hash doesn't exist: hash={} ";

    @Async("hashGeneratorExecutor")
    @Transactional
    public void save(String hash, String url) {
        jdbcTemplate.update(
                "INSERT INTO url (hash, url) VALUES (?, ?)",
                hash, url
        );
    }

    @Transactional
    public String find(String hash) {
        try {
            return jdbcTemplate.queryForObject(
                    "Select url FROM url WHERE hash=?",
                    String.class,
                    hash
            );
        } catch (EmptyResultDataAccessException e) {
            log.error(ERROR_HASH_DOES_NOT_EXIST, hash);
            throw new HashNotFoundException(ERROR_HASH_DOES_NOT_EXIST + hash);
        }
    }

    public List<String> findTop(int number) {
        return jdbcTemplate.query(
                "SELECT hash FROM url LIMIT ?",
                (rs, rowNum) -> rs.getString("hash"),
                number
        );
    }

    /**
     * 1. Inserting hashes from 'url' table(that will be deleted) into 'hash' table
     * 2. Removing of old records from 'url' table
     */
    @Transactional
    public void moveOldHashesToHashTable(int days) {
        jdbcTemplate.update(
                "INSERT INTO hash (hash) " +
                        "SELECT DISTINCT u.hash FROM url u " +
                        "WHERE u.created_at < CURRENT_TIMESTAMP - (INTERVAL '1 day' * ?) ",
                days
        );

        jdbcTemplate.update(
                "DELETE FROM url WHERE created_at < CURRENT_TIMESTAMP - (INTERVAL '1 day' * ?)",
                days
        );
    }

    @Transactional
    public List<String> findOldHashes(int days) {
        return jdbcTemplate.queryForList(
                "SELECT u.hash FROM url u " +
                        "WHERE u.created_at < CURRENT_TIMESTAMP - (INTERVAL '1 day' * ?)",
                String.class,
                days
                );
    }
}
