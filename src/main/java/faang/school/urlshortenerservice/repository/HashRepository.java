package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashRepository {
    private final JdbcTemplate jdbcTemplate;
    private final HashConfig hashConfig;

    public List<Long> getUniqueNumbers(@NotNull @Positive Integer numberOfElements) {
        String sql = "SELECT nextval('unique_number_seq') FROM generate_series(1, ?)";
        return jdbcTemplate.queryForList(sql, Long.class, numberOfElements);
    }

    public void saveAll(@NotNull List<String> hashes) {
        if (hashes.isEmpty()) {
            log.info("Nothing to save, received empty list of hashes");
            return;
        }
        String sql = "INSERT INTO hash (hash_value) VALUES (?)";

        int partitionSize = hashConfig.getPartitionSize();
        int batchSize = Math.min(partitionSize, hashes.size());
        List<List<String>> partitions = ListUtils.partition(hashes, batchSize);

        partitions.forEach(partition -> jdbcTemplate.batchUpdate(sql, partition, partition.size(),
                (ps, hash) -> ps.setString(1, hash)));
        log.info("Successfully saved all {} hashes", hashes.size());
    }

    public List<String> getAndDeleteHashBatch() {
        long fetchLimit = hashConfig.getFetchLimit();
        log.info("Deleting hashes with limit={}", fetchLimit);
        String sql = """
                DELETE FROM hash 
                WHERE hash_value IN (
                    SELECT hash_value FROM hash 
                    LIMIT ? 
                    FOR UPDATE SKIP LOCKED
                ) 
                RETURNING hash_value
                """;
        return jdbcTemplate.queryForList(sql, String.class, fetchLimit);
    }
}