package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUrlRepository {

    private final JdbcTemplate jdbc;

    public boolean save(String hash, String url, LocalDateTime createdAt) {
        try {
            jdbc.update(
                    "INSERT INTO urls(hash, url, created_at) VALUES (?, ?, ?)",
                    hash, url, createdAt
            );
            return true;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }


    public Optional<String> findUrlByHash(String hash) {
        List<String> list = jdbc.query(
                "SELECT url FROM urls WHERE hash = ?",
                (ResultSet rs, int rowNum) -> rs.getString("url"),
                hash
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
