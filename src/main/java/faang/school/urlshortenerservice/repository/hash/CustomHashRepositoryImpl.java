package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class CustomHashRepositoryImpl implements CustomHashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllInBatch(List<Hash> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash(hash) VALUES(?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Hash hash = hashes.get(i);
                        ps.setString(1, hash.getHash());
                    }

                    @Override
                    public int getBatchSize() {
                        return hashes.size();
                    }
                });
    }
}