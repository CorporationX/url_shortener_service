package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class HashCache {
    @Value("${hash.cached-count}")
    private int cachedCount;

    private final LinkedBlockingQueue<String> hashes;
    private final ExecutorService service;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    public HashCache(ExecutorService service, HashRepository hashRepository, HashGenerator hashGenerator) {
        this.service = service;
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;

        this.hashes = new LinkedBlockingQueue<>(cachedCount);
    }

    @PostConstruct
    public void initHashes() {
        hashGenerator.generateBatch();
        hashes.addAll(hashRepository.getHashBatch());
    }

    public String getHash() {
        if (hashes.remainingCapacity() * 0.25 >= hashes.size()) {
            fillTheHashes();
        }

        return hashes.poll();
    }

    private synchronized void fillTheHashes() {
        if (hashes.remainingCapacity() * 0.25 >= hashes.size()) {
            service.execute(() -> hashes.addAll(hashRepository.getHashBatch()));
            service.execute(hashGenerator::generateBatch);
        }
    }
}
