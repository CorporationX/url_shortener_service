package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${hash.count-of-numbers-from-the-sequence}")
    private int cachedCount;

    private LinkedBlockingQueue<String> hashes;
    private final ExecutorService service;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void initHashes() {
        cachedCount = (int) (cachedCount / 0.8);
        hashes = new LinkedBlockingQueue<>(cachedCount);

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
