package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.ShortLinkHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashFabricator {

    @Value("${hash.cache.capacity}")
    private int hashCapacity;

    private final ShortLinkHashRepository repository;
    private final Base62Encoder encoder;

    @Transactional
    @Scheduled(cron = "${hash.cache.cron.create}")
    public void getHashBatch() {
        List<String> saveHash = encoder.encode(repository.getListSequences(hashCapacity));
        List<Hash> hashes = saveHash.stream().map(Hash::new)
                .toList();
        repository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = repository.getHashBatch(amount);
        if (hashes.size() < amount) {
            getHashBatch();
            hashes.addAll(repository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Transactional
    @Async("shortLinkThreadPoolExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
