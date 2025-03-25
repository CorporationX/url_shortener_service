package faang.school.url_shortener_service.hash_recycling;

import faang.school.url_shortener_service.entity.Hash;
import faang.school.url_shortener_service.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecycledHashHandler {
    private final HashRepository hashRepository;
    private final CacheManager cacheManager;

    @Async("oldUrlDeletionExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveRecycledHashes(List<Hash> hashes) {
        String thread = Thread.currentThread().getName();
        log.debug("Recycling {} hashes [thread: {}]...", hashes.size(), thread);
        List<Hash> saved = hashRepository.saveAll(hashes);
        saved.forEach(hash -> {
                    try {
                        Cache cache = cacheManager.getCache("urls");
                        if (cache != null) {
                            cache.evict(hash.getHash());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to evict cache for hash {} [thread: {}]", hash.getHash(), thread, e);
                    }
                }
        );
        log.debug("Recycling complete for {} hashes [thread: {}]...", saved.size(), thread);
        return CompletableFuture.completedFuture(null);
    }
}