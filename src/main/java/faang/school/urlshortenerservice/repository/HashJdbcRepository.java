package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class HashJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:10000}")
    private int batchSize;

    public void batchInsert(List<String> hashes) {
        for (int i = 0; i < hashes.size(); i += batchSize) {
            List<String> batch = hashes.subList(i, Math.min(i + batchSize, hashes.size()));
            jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)", batch, batch.size(),
                    (ps, hash) -> ps.setString(1, hash));
        }
    }
}
