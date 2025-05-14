package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<String> findTop(int number) {
        return jdbcTemplate.query(
                "SELECT hash FROM url LIMIT ?",
                (rs, rowNum) -> rs.getString("hash"),
                number
        );
    }
}
