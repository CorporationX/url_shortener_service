package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashServiceImpl implements HashService {

    @Value("${services.hash.batch.size}")
    private Long batchSize;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Override
    @Transactional
    @Async("generateBatchExecutor")
    public void generateBatch() {
        List<Hash> hashes = base62Encoder.encodeSequence(hashRepository.findUniqueSequence(batchSize)).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
        log.info("Generated {} hashes", hashes.size());
    }

    @Override
    @Transactional
    public List<String> getHashes(Long amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        while (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Override
    @Transactional
    @Async("generateBatchExecutor")
    public CompletableFuture<List<String>> getHashesAsync(Long amount) {
        log.info("Fetching hashes from batch");
        return CompletableFuture.supplyAsync(() -> getHashes(amount));
    }

    @Override
    public void saveAllHashes(List<String> hashes) {
        hashRepository.saveAll(hashes.stream().map(Hash::new).toList());
    }
}
