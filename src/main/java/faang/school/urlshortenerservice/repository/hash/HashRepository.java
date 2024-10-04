package faang.school.urlshortenerservice.repository.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash.batch.size}")
    private Integer batchSize;
    @Value("${hash.sequence.name}")
    private String uniqueSequenceName;

    private final JdbcTemplate jdbcTemplate;
    private final HashJpaRepository hashJpaRepository;

    public void batchSave(List<String> hashes) {
        jdbcTemplate.batchUpdate("INSERT INTO hash (HASH) VALUES (?)",
                hashes,
                batchSize,
                (PreparedStatement preparedStatement, String hash) -> preparedStatement.setString(1, hash)
        );
    }

    public List<Long> getUniqueValues() {
        return hashJpaRepository.getUniqueValues(uniqueSequenceName, batchSize);
    }

    public List<String> getHashBatch() {
        return hashJpaRepository.getHashBatch(batchSize);
    }
}
