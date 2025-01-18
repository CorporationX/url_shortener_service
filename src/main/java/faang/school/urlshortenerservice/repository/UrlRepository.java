package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<String> deleteOldUrlsAndGetHashes() {
        String query = "WITH deleted AS ( DELETE FROM url WHERE created_at < CURRENT_DATE - INTERVAL '1 year' RETURNING hash) SELECT hash FROM deleted";
        return jdbcTemplate.queryForList(query, String.class);
    }

    public void saveUrl(String hash, String url) {
        String checkQuery = "SELECT COUNT(1) FROM url WHERE hash = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, hash);
        if (count == null || count == 0) {
            String query = "INSERT INTO url (hash, url) VALUES (?, ?)";
            jdbcTemplate.update(query, hash, url);
        } else {
            // Логирование или исключение
            throw new DuplicateKeyException("Hash already exists: " + hash);
        }
    }

    public String getUrlByHash(String hash) {
        String query = "SELECT url FROM url WHERE hash = ?";
        try {
            return jdbcTemplate.queryForObject(query, String.class, hash);
        } catch (EmptyResultDataAccessException e) {
            // Логируем или возвращаем null
            return null;
        }
    }
}
