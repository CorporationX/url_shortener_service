package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.HashBatchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryCustomImpl implements HashRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    private final HashBatchProperties properties;
    private static final String SAVE_HASH_VALUES = "INSERT INTO hash (hash) VALUES (?)";

    @Override
    @Transactional
    public void saveAll(List<String> hashes) {
        List<List<String>> batches = ListUtils.partition(hashes, properties.getBatchSize());
        batches.forEach(batch -> {
            jdbcTemplate.batchUpdate(SAVE_HASH_VALUES, batch, properties.getBatchSize(),
                    (ps, hash) -> ps.setString(1, hash));
            log.info("Saved batch of hashes. Batch size: {}", batch.size());
        });
    }
}
