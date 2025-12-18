package faang.school.urlshortenerservice.services;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class LocalHashService {
    private final HashGenerator generator;
    
    @Value("${url-shortener.hash.range}")
    private int capacity;
    
    @Value("${url-shortener.hash.fill-percent}")
    private int minPercent;
    
    private Queue<String> hashes;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(generator.getHash(capacity));
    }

    public String getHash() {
        if (hashes.size() < capacity * minPercent / 100) {
            if (filling.compareAndSet(false, true)) {
                generator.getHashAsync(capacity).thenAccept(hashes::addAll);
                filling.set(false);
            }
        }
        return hashes.poll();
    }
}
