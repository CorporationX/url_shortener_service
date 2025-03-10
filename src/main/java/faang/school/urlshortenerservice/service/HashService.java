package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashService {

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.generator.max_range:10000}")
    private int maxRange;

    @Transactional
    public void generateHashes() {
        List<Long> numbers = hashRepository.getNextRange(maxRange);
        List<String> hashes = numbers.stream()
                .map(base62Encoder::encode)
                .toList();

        saveHashes(hashes);
    }

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        log.info("Request get Hashes");
        return hashes;
    }

    @Async("hashThreadExecutor")
    @Transactional
    public CompletableFuture<List<Hash>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        List<Object[]> batchArgs = hashes.stream()
                .map(hash -> new Object[] {hash})
                .toList();
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
