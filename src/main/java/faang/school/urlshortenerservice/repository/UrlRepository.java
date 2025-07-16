package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void save(String hash, String url) {
        String sql = "INSERT INTO url (hash, url) values (?, ?) ON CONFLICT (hash) DO NOTHING";
        jdbcTemplate.update(sql, hash, url);
    }

    @Transactional
    public Optional<String> findByHash(String hash) {
        String sql = "SELECT url FROM url WHERE hash = ?";
        List<String> url = jdbcTemplate.query(sql, new Object[]{hash},
                (rs, rowNum) -> rs.getString("url"));
        return url.stream().findFirst();
    }
}
