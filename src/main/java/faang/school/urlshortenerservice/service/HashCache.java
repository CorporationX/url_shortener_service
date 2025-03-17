package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashCache {

    private final Queue<Hash> hashes = new ConcurrentLinkedQueue<>();

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash-queue.max-size}")
    private int QUEUE_SIZE;

    @Value("${hash-queue.refresh-percentage}")
    private String REFRESH_PERCENTAGE;

    public Hash getHash() {
        int min = (int) (QUEUE_SIZE * Double.parseDouble(REFRESH_PERCENTAGE));
        if (hashes.size() < min) {
            fillHashes();
        }
        if (hashes.isEmpty()) {
            throw new HashNotFoundException("Internal hash queue is empty. Please try again.");
        }
        Hash hash = hashes.poll();
        log.info("Polled hash from the queue {}", hash);
        return hash;
    }

    @Async("threadPoolExecutor")
    public void fillHashes() {
        List<Hash> batch = hashRepository.getHashBatch(QUEUE_SIZE);
        hashes.addAll(batch);
        log.info("{} hashes added to the internal queue", hashes.size());
        hashGenerator.generateBatch();
    }

}
