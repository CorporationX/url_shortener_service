package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    @Value("${hash.capacity}")
    private int capacity =10;
    @Value("${hash.check-percent}")
    private int percent;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final Queue<Hash> hashes = new ArrayBlockingQueue<>(capacity);

    @PostConstruct
    public void init() {
        hashGenerator.getHashesAsync(capacity).thenAccept(hashes::addAll);
        System.out.println(hashes.toString());
    }

//    @Async("executorService")
    public String getHash() {
        if (hashes.size() / capacity * 100 < percent) {
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll().getHash();
    }
}