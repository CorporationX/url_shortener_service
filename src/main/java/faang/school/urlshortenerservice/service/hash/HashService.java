package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.cache.HashCache;
import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.hash.FreeHashRepository;
import faang.school.urlshortenerservice.service.sequence.UniqueNumberService;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashConfig hashConfig;
    private final Base62Encoder base62Encoder;
    private final FreeHashRepository freeHashRepository;
    private final UniqueNumberService uniqueNumberService;

    @Transactional
    public void saveRangeHashes(List<String> hashes) {
        freeHashRepository.saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes() {
        List<String> hashes = freeHashRepository.findAndDeleteFreeHashes(hashConfig.getSelectRange());
        if (hashes.size() < hashConfig.getSelectRange()) {
            generateBatchHash();
            hashes.addAll(freeHashRepository.findAndDeleteFreeHashes(hashConfig.getSelectRange() - hashes.size()));
        }
        return hashes;
    }

    @Async("urlThreadPool")
    public CompletableFuture<List<String>> getHashesAsync() {
        return CompletableFuture.supplyAsync(this::getHashes);
    }

    @Transactional
    @Async("urlThreadPool")
    public void generateBatchHash() {
        List<Long> numbers = uniqueNumberService.getUniqueNumbers();
        List<String> base62Hashes = base62Encoder.encodeListNumbers(numbers);
        saveRangeHashes(base62Hashes);
    }
}
