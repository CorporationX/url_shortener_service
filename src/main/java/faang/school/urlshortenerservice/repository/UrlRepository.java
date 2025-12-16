package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<String> deleteOldUrlHashes() {
        String sql = """
                 DELETE
                   FROM url
                   WHERE created_at < NOW() - INTERVAL '1 year'
                 RETURNING hash
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("hash"));
    }

    public void save(Url url) {
        String sql = """
                INSERT
                  INTO url (hash, url)
                VALUES (?, ?)
                """;
        jdbcTemplate.update(sql, url.getHash(), url.getUrl());
    }

    public Url findByHash(String hash) {
        String sql = """
                SELECT hash, url, created_at
                  FROM url
                 WHERE hash = ?""";
        return jdbcTemplate.queryForObject(
                sql,
                (rs, rownum) -> new Url(
                        rs.getString("hash"),
                        rs.getString("url"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ),
                hash);
    }
}
