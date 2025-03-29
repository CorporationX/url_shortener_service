package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RequiredArgsConstructor
public class CustomHashRepositoryImpl implements CustomHashRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${hashes.insert-batch-size}")
    private int insertBatchSize;

    @Override
    public void batchInsert(List<Hash> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hashes (hash) VALUES (?)",
                hashes,
                insertBatchSize,
                (ps, hash) -> ps.setString(1, hash.getHash())
        );
    }
}
