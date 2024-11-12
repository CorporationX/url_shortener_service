package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.range:1000}")
    private int range;

    @Transactional
    @Async("generateBatchExecutor")
    public void generateBatch() {
        System.out.println("thread "+Thread.currentThread().getName());
        Long start = System.currentTimeMillis();

        // TODO remove commemnts and remake hashesToSave try remove @OneToOne from Hash
        List<Long> hashes = hashRepository.getNextRange(range);
        Set<String> hashesSet = base62Encoder.encode(hashRepository.getNextRange(range));
        List<String> listt = hashesSet.stream().toList();

        String sql = "INSERT INTO hash (hash) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, listt.get(i));
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
        Long end = System.currentTimeMillis();
        System.out.println("duration " + (end - start));
    }

}
