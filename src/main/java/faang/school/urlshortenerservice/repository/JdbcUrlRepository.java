package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
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

    /**
     * Возвращает hash, привязанный к данному url
     * (существующий или только что сгенерированный).
     */
    public String saveOrGet(String hash, String url, LocalDateTime createdAt) {
        return jdbc.queryForObject(
                """
                INSERT INTO urls(hash, url, created_at)
                VALUES (?, ?, ?)
                ON CONFLICT (url)
                DO UPDATE SET hash = urls.hash
                RETURNING hash
                """,
                String.class,
                hash, url, createdAt
        );
    }



    public Optional<String> findUrlByHash(String hash) {
        List<String> list = jdbc.query(
                "SELECT url FROM urls WHERE hash = ?",
                (ResultSet rs, int rowNum) -> rs.getString("url"),
                hash
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<String> deleteOldAndReturnHashes(LocalDateTime threshold) {
        return jdbc.query(
                "DELETE FROM urls WHERE created_at < ? RETURNING hash",
                (rs, rowNum) -> rs.getString("hash"),
                threshold
        );
    }
}
