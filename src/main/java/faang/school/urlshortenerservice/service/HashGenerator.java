package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.BaseEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${hash.generator.batch-size:1000}")
    private long batchSize;

    @Transactional
    @Scheduled(cron = "${hash.generator.cron:0*****}")
    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = baseEncoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<String> hashes = hashRepository.getHashBatchAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatchAndDelete(amount- hashes.size()));
        }
        return hashes;
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));

    }
}
