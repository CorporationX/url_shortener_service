package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Url> rowMapper = new RowMapper<>() {
        @Override
        public Url mapRow(ResultSet rs, int rowNum) throws SQLException {
            Url url = new Url();
            url.setHash(rs.getString("hash"));
            url.setUrl(rs.getString("url"));
            url.setCreatedAt(rs.getTimestamp("created_at"));
            return url;
        }
    };

    @Override
    public Optional<Url> findByUrl(String url) {
        String sql = "SELECT * FROM url WHERE url = ?";
        List<Url> result = jdbcTemplate.query(sql, rowMapper, url);
        return result.stream().findFirst();
    }

    @Override
    public Optional<Url> findById(String hash) {
        String sql = "SELECT * FROM url WHERE hash = ?";
        List<Url> result = jdbcTemplate.query(sql, rowMapper, hash);
        return result.stream().findFirst();
    }

    @Override
    public void save(Url url) {
        String sql = "INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, url.getHash(), url.getUrl(), url.getCreatedAt());
    }

    @Override
    public List<String> deleteOldUrlsBefore(Timestamp cutoff) {
        String sql = "DELETE FROM url WHERE created_at < ? RETURNING hash";
        return jdbcTemplate.queryForList(sql, String.class, cutoff);
    }
}
