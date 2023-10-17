package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Base62Encoder base62Encoder;

    @Value("${hash.maxRange}")
    private int maxRange;
    @Value("${hash.batchSize}")
    private int batchSize;

    private static final String SQL_INSERT_HASH = "INSERT INTO hash (hash) VALUES (?)";

    public void saveHashes(List<String> hashes) {

        String sql = "INSERT INTO hash (hash) VALUES(?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i));
            }

            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Transactional(readOnly = true)
    public List<Long> getUniqueNumbers(int maxRange) {
        return hashRepository.getUniqueNumbers(maxRange);
    }

    @Transactional
    public void saveHashesBatch(List<Long> uniqueNumbers) {
        List<String> encodedHashes = base62Encoder.encodeNumbers(uniqueNumbers);

        jdbcTemplate.batchUpdate(SQL_INSERT_HASH, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, encodedHashes.get(i));
            }

            @Override
            public int getBatchSize() {
                return encodedHashes.size();
            }
        });
    }

    @Transactional
    List<HashEntity> getHashBatch(int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }
}
