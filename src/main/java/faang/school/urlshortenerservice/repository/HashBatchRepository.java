package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${url-shortener.batch-size}")
    private int batchSize;

    @Transactional
    public void saveHashByBatch(List<Hash> hashes) {

        jdbcTemplate.batchUpdate("INSERT INTO hash (hash) VALUES (?)", hashes,
                batchSize,
                (ps, argument) -> ps.setString(1, argument.getHash()));
    }
}
