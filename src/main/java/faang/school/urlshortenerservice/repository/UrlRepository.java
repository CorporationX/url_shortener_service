package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.RedisUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

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
            throw new RuntimeException("Hash is not found in DB");
        }
    }
}
