package faang.school.urlshortenerservice.cache.impl;

import faang.school.urlshortenerservice.cache.HashCache;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    private static final String HASH_NOT_FOUND_MESSAGE = "Hash was not found";
    private static final String AVAILABLE_CACHED_HASH_MESSAGE = "Available cached hash size is less than 20 %";

    private final UrlShortenerProperties properties;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final HashGenerator hashGenerator;
    private final ReentrantLock locker = new ReentrantLock();
    private ArrayBlockingQueue<Hash> queue;

    @PostConstruct
    public void init() {
        queue = new ArrayBlockingQueue<>(properties.getBatchSizeMax());
        cacheHashesIfNeeded();
    }

    @Override
    public String getHash() {
        executorService.execute(this::cacheHashesIfNeeded);
        Hash poll = queue.poll();
        if (poll == null) {
            throw new HashNotFoundException(HASH_NOT_FOUND_MESSAGE);
        }
        return poll.getHash();
    }

    private void cacheHashesIfNeeded() {
        if (!locker.isLocked()) {
            if (isLowerOfMinPercentageBound()) {
                log.debug(AVAILABLE_CACHED_HASH_MESSAGE);
                locker.lock();
                hashGenerator.generateBatch();
                List<Hash> newHashes = hashRepository.getHashBatch(properties.getBatchSizeMax() - queue.size());
                queue.addAll(newHashes);

            }
            locker.unlock();
        }
    }

    private boolean isLowerOfMinPercentageBound(){
        return (double) queue.size() / properties.getBatchSizeMax() * 100 < properties.getLowerBoundPercentageFill();
    }
}
