package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.config.properties.FetchProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.FreeHashNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCacheServiceImpl implements HashCacheService {

    private final HashRepository hashRepository;
    private final HashGeneratorService hashGeneratorService;
    private final ExecutorService executorService;
    private final FetchProperties fetchProperties;

    private final Queue<String> freeHashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isQueueReplenishedHashes = new AtomicBoolean(false);

    @PostConstruct
    public void initFreeHashes() {
        fetchFreeHashes();
    }

    @Override
    public String getHash() {
        if (freeHashes.size() <= fetchProperties.getLimitOnReplenishment() &&
                isQueueReplenishedHashes.compareAndSet(false, true)) {
            fetchFreeHashes();
        }
        return Optional.ofNullable(freeHashes.poll())
                .or(hashRepository::getHash)
                .orElseThrow(() -> new FreeHashNotFoundException("Free hash not found!"));
    }

    @Override
    public void addHash(List<String> hashes) {
        List<Hash> entityHashes = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(entityHashes);
    }

    private void fetchFreeHashes() {
        executorService.execute(() -> {
            hashGeneratorService.generateFreeHashes();
            List<String> newFreeHashes = hashRepository.getHash(fetchProperties.getBatchSize());
            freeHashes.addAll(newFreeHashes);
            isQueueReplenishedHashes.set(false);
        });
    }
}
