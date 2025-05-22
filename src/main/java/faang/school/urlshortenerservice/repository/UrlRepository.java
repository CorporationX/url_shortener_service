package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.NotFoundException;
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
    private static final String ERROR_HASH_DOES_NOT_EXIST = "Hash doesn't exist: hash={}";

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
            throw new NotFoundException("Hash is not found: " + hash);
//            throw new RuntimeException("Hash is not found in DB");
        }
    }

    public List<String> findTop(int number) {
        return jdbcTemplate.query(
                "SELECT hash FROM url LIMIT ?",
                (rs, rowNum) -> rs.getString("hash"),
                number
        );
    }

    public List<String> deleteOldHashesFromUrlAndReuse() {
        return jdbcTemplate.queryForList(
                "INSERT deleted into hash WITH deleted AS (" +
                        "   DELETE FROM url " +
                        "   WHERE created_at = 2025-05-10 14:21:19.184 +0300 " +
                        "   RETURNING hash" +
                        ") ",
                String.class
        );
    }

    @Transactional
    public void moveOldHashesToHashTable() {
        // 1. Вставка хэшей из удаляемых записей в таблицу hash
        jdbcTemplate.update(
                "INSERT INTO hash (hash) " +
                        "SELECT DISTINCT u.hash FROM url u " +
                        "WHERE u.created_at < CURRENT_TIMESTAMP - INTERVAL'2 minute' "
//                      "WHERE u.created_at < CURRENT_DATE - INTERVAL'1 day' "
        );

        // 2. Удаление старых записей из таблицы url
        jdbcTemplate.update(
//                "DELETE FROM url WHERE created_at < CURRENT_DATE - INTERVAL'1 day'"
                "DELETE FROM url WHERE created_at < CURRENT_TIMESTAMP - INTERVAL'2 minute' "

        );
    }

    @Transactional
    public List<String> findOldHashes() {
        return jdbcTemplate.query(
                "SELECT u.hash FROM url u " +
//                        "WHERE u.created_at < CURRENT_DATE - INTERVAL'1 day' ",
                "WHERE u.created_at < CURRENT_TIMESTAMP - INTERVAL'2 minute' ",
                (rs, rowNum) -> rs.getString("hash")
        );
    }
}
