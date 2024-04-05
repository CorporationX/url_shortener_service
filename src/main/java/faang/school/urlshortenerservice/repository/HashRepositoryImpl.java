package faang.school.urlshortenerservice.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
@RequiredArgsConstructor
public class HashRepositoryImpl implements HashRepository {

    private final JdbcTemplate jdbcTemplate;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Value("${hashes.repository.hashes-batch}")
    private int hashesBatch;

    @Override
    @Transactional
    public List<Long> getUniqueNumbers(int amount) {
        List<Long> uniqueNumbers = new ArrayList<>();
        String sql = "SELECT nextval('unique_number_sequence');";
        for (int i = 0; i < amount; i++) {
            Long nextNumber = jdbcTemplate.queryForObject(sql, Long.class);
            uniqueNumbers.add(nextNumber);
        }
        return uniqueNumbers;
    }

    @Override
    @Transactional
    public void save(Set<String> hashes) {
        String sql = "INSERT INTO hashes (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql,
                hashes,
                batchSize,
                (preparedStatement, hash) -> preparedStatement.setString(1, hash));
    }

    @Override
    public ConcurrentLinkedQueue<String> getHashBatch() {
        String sql = """
                WITH deleted AS (
                    DELETE FROM hashes
                    WHERE hash IN(
                        SELECT hash FROM hashes
                        LIMIT ?
                    )
                    RETURNING hash
                )
                SELECT hash FROM deleted;""";
        List<String> hashes = jdbcTemplate.queryForList(sql, String.class, hashesBatch);
        return new ConcurrentLinkedQueue<>(hashes);
    }
}
