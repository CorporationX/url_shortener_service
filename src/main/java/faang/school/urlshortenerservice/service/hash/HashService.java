package faang.school.urlshortenerservice.service.hash;

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
    public void saveHashes(List<String> hashes) {
        freeHashRepository.saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes() {
        int batchHashesSelect = hashConfig.getSelectBatch();

        List<String> hashes = freeHashRepository.findAndDeleteFreeHashes(batchHashesSelect);

        if (hashes.size() < batchHashesSelect) {
            generateBatchHash(batchHashesSelect - hashes.size())
                    .thenAccept(hashes::addAll);
        }

        return hashes;
    }

    @Async("urlThreadPool")
    public CompletableFuture<List<String>> getHashesAsync() {
        return CompletableFuture.supplyAsync(this::getHashes);
    }

    @Transactional
    @Async("urlThreadPool")
    public CompletableFuture<List<String>> generateBatchHash(int needHashes) {
        List<Long> numbers = uniqueNumberService.getUniqueNumbers();
        List<String> base62Hashes = base62Encoder.encodeNumbersInBase62(numbers);

        List<String> response = base62Hashes.subList(0, Math.min(needHashes, base62Hashes.size()));
        List<String> remainingHashes = base62Hashes
                .subList(Math.min(needHashes, base62Hashes.size()), base62Hashes.size());

        saveHashes(remainingHashes);
        return CompletableFuture.completedFuture(response);
    }
}
