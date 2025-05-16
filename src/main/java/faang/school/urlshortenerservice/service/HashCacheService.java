package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Service
@RequiredArgsConstructor
public class HashCacheService {
    @Value("${spring.hash.queue-capacity:10000}")
    private int queueCapacity;
    private final ArrayBlockingQueue<String> hashCashQueue;

    public void addHashesToQueue(List<String> hashes) {
        hashCashQueue.addAll(hashes);
    }

    public String popHash() {
        return hashCashQueue.poll();
    }

    public int getQueueSize() {
        return hashCashQueue.size();
    }
}
