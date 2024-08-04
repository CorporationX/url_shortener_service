package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCash {

    @Value("${cash.size:10000}")
    private final int cashSize=1;
    @Value("${cash.threshold:0.2}")
    private double threshold=0.2;

    private final Queue<String> localHashes = new ArrayBlockingQueue<>(cashSize);
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public String getHash() {
        int minSize = (int) (cashSize * threshold);
        if (localHashes.size() < minSize) {
            if (isRunning.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(cashSize)
                        .thenAccept(localHashes::addAll)
                        .thenRun(() -> isRunning.set(false));
            } 
        }
        return localHashes.poll();
    }

    @PostConstruct
    void initiateCash() {
        hashGenerator.generateBatch();
        List<Hash> hashes = hashRepository.getHashBatch(cashSize);
        localHashes.addAll(hashes.stream().map(Hash::getHash).toList());
    }
}
