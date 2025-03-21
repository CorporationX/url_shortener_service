package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.HashBatchProperties;
import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryCustomImpl implements HashRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    private final HashBatchProperties properties;

    @Override
    @Transactional
    public void save(List<Hash> hashes) {
        List<List<Object[]>> batches = createBatches(hashes);
        batches.forEach(batch -> {
            saveHashBatch(batch);
            log.info("Saved batch of hashes. Batch size: {}", batch.size());
        });
    }

    @Transactional
    private void saveHashBatch(List<Object[]> hashes) {

        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, hashes);
    }

    private List<List<Object[]>> createBatches(List<Hash> freeHashes) {
        int batchSize = properties.getBatchSize();
        return IntStream.range(0, (freeHashes.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> freeHashes.subList(i * batchSize, Math.min((i + 1)
                        * batchSize, freeHashes.size())))
                .map(batch -> batch.stream()
                        .map(hash -> new Object[]{hash.getHash()})
                        .toList())
                .toList();
    }
}
