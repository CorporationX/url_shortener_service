package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.property.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final UrlShortenerProperties properties;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final HashGenerator hashGenerator;
    private final ReentrantLock locker = new ReentrantLock();
    private ArrayBlockingQueue<Hash> queue;

    @PostConstruct
    public void init() {
        queue = new ArrayBlockingQueue<>(properties.getBatchSizeMax());
        cacheIfHashesOnLowerBound();
    }

    public String getHash() {
        executorService.execute(this::cacheIfHashesOnLowerBound);
        Hash poll = queue.poll();
        if (poll == null) {
            throw new HashNotFoundException();
        }
        return poll.getHash();
    }

    public void cacheIfHashesOnLowerBound() {
        if (!locker.isLocked()) {
            if (isLowerOfMinPercentageBound()) {
                locker.lock();
                hashGenerator.generateBatch();
                List<Hash> newHashes = hashRepository.getHashBatch(properties.getBatchSizeMax());
                queue.addAll(newHashes);

            }
            locker.unlock();
        }
    }

    private boolean isLowerOfMinPercentageBound(){
        return queue.size() / properties.getBatchSizeMax() * 100 < properties.getLowerBoundPercentageFill();
    }
}
