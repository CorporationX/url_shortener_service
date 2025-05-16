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
            log.error(ERROR_HASH_DOES_NOT_EXIST,hash);
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
}
