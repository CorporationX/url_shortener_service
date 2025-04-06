package faang.school.urlshortenerservice.repository.Url;

import faang.school.urlshortenerservice.model.Url;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcImpl implements UrlRepositoryJdbc  {
    @Value("${hash.batch.size}")
    private int batchSize;

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveUrlsBatch(List<Url> urls) {
        String sql = "INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Url url = urls.get(i);
                ps.setString(1, url.getHash());
                ps.setString(2, url.getUrl());
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(url.getCreatedAt()));
            }

            @Override
            public int getBatchSize() {
                return urls.size();
            }
        });
    }
}
