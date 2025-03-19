package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final JdbcTemplate jdbcTemplate;
    private final Base62Encoder base62Encoder;
    private final HashCacheProperties properties;

    public void generateAdditionalHashes(int count) {
        saveHashesBatch(base62Encoder.encode(getUniqueNumbers(count)));
    }

    private List<Long> getUniqueNumbers(int count) {
        return jdbcTemplate.queryForList("SELECT nextval('url_hash_seq') FROM generate_series(1, ?)",
                Long.class,
                count);
    }

    private void saveHashesBatch(List<String> hashes) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO hash (hash) VALUES (?)",
                hashes,
                properties.getBatchSize(),
                ((ps, hash) -> ps.setString(1, hash))
        );
    }
}
