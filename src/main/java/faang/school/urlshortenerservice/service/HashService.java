package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.generate.amount:10000}")
    private int hashAmount;

    @Async("hashGeneratorExecutor")
    @Transactional
    public CompletableFuture<Void> generateBatch() {
        log.info("Starting asynchronous batch generation of {} hashes", hashAmount);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashAmount);
        List<String> encodedHashes = base62Encoder.encode(uniqueNumbers);
        saveHashes(encodedHashes);
        log.info("Batch generation complete, saved {} hashes", encodedHashes.size());
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = new ArrayList<>(hashRepository.getHashBatch(amount));
        if (hashes.size() < amount) {
            log.info("Not enough hashes available (requested {}, found {}), generating more", amount, hashes.size());
            generateBatch();
            List<Hash> additionalHashes = hashRepository.getHashBatch(amount - hashes.size());
            hashes.addAll(additionalHashes);
        }
        log.info("Fetched {} hashes from database", hashes.size());
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHashes(List<String> freedHashes) {
        if (freedHashes.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO hash (hash) VALUES (?)";
        jdbcTemplate.batchUpdate(
                sql,
                freedHashes,
                freedHashes.size(),
                (ps, hash) -> ps.setString(1, hash)
        );
    }
}
