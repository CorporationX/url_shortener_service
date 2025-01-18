package faang.school.url_shortener_service.scheduler;

import faang.school.url_shortener_service.entity.Hash;
import faang.school.url_shortener_service.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UrlCleanerAsync {
    private final HashRepository hashRepository;
    private final CacheManager cacheManager;

    @Async("executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanUrl(List<Hash> hashes) {
        List<Hash> saved = hashRepository.saveAll(hashes);
        saved.forEach(hash ->
                Objects.requireNonNull(cacheManager.getCache("urls")).evict(hash.getHash()));
    }
}