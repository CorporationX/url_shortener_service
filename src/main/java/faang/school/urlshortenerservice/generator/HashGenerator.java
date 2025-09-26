package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;

    private final Executor hashGeneratorExecutorService;

    private final Base62Encoder base62Encoder;

    public HashGenerator(
            HashRepository hashRepository,
            @Qualifier("hashGeneratorExecutorService") Executor hashGeneratorExecutorService,
            Base62Encoder base62Encoder
    ) {
        this.hashRepository = hashRepository;
        this.hashGeneratorExecutorService = hashGeneratorExecutorService;
        this.base62Encoder = base62Encoder;
    }

    @Value("${hash.generator.generate-batch-size:100}")
    private int maxRange;

    @Value("${hash.generator.save-batch-size:10}")
    private int batchSize;

    public void generateHashes(){
        List<Long> range = hashRepository.getNextRange(maxRange);
        List<Hash> hashList = base62Encoder.base62EncodeList(range).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAllBatched(hashList, batchSize);
    }

    public CompletableFuture<List<String>> getHashes(long amount) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> hashes = new ArrayList<>(hashRepository.findAndDelete(amount));
            log.info("Got {} amount of hashes from DB", hashes.size());
            while (hashes.size() < amount) {
                generateHashes();
                List<String> newHashes = hashRepository.findAndDelete(amount - hashes.size());
                if (newHashes.isEmpty()){
                   throw new IllegalStateException("Could not generate or retrieve any new hashes.");
                }
                hashes.addAll(newHashes);
            }
            return hashes;
        }, hashGeneratorExecutorService);
    }
}
