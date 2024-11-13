package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.config.FetchProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isReplenishing = new AtomicBoolean(false);

    @PostConstruct
    public void initFreeHashes() {
        fetchFreeHashes();
    }

    @Override
    public String getHash() {
        if (hashes.size() <= fetchProperties.getLimitOnReplenishment() &&
                isReplenishing.compareAndSet(false, true)) {
            fetchFreeHashes();
        }
        return Optional.ofNullable(hashes.poll())
                .or(hashRepository::getHash)
                .orElseThrow(() -> new RuntimeException("Free hash not found!"));
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
            hashGeneratorService.generateBatch();
            List<String> newFreeHashes = hashRepository.getHashes(fetchProperties.getBatchSize());
            hashes.addAll(newFreeHashes);
            isReplenishing.set(false);
        });
    }
}
