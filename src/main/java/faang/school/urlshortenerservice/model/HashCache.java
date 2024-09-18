package faang.school.urlshortenerservice.model;


import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class HashCache {

    private final ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.max_hash_cash: 10000}")
    private long maxHashCash;
    @Value("${hash.min_percent: 20}")
    private long minPercent;

    public Hash getHash() {
        if (concurrentLinkedQueue.size() <= minPercent * maxHashCash / 100) {
            executorService.execute(() -> {
                List<Hash> hashes = hashRepository.getHashBatch(
                        maxHashCash - minPercent * maxHashCash);
                List<String> strings = hashes.stream()
                        .map(Hash::getHash)
                        .toList();
                concurrentLinkedQueue.addAll(strings);
            });
            hashGenerator.generateBatch();
        }
        return new Hash(concurrentLinkedQueue.poll());
    }
}
