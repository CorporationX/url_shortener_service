package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;


    @Value("${hash.generator.max_range:100}")
    private int maxRange;

    @Transactional
    public void generateHashes() {
        List<Long> numbers = hashRepository.getNextRange(maxRange);
        if (numbers.isEmpty()) {
            return;
        }
        List<String> hashes = base62Encoder.encode(numbers);
        saveHashes(hashes);
    }


    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHashes();
            List<Hash> additionalHashes = hashRepository.findAndDelete(amount - hashes.size());
            hashes.addAll(additionalHashes);
        }

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }

        List<Hash> hashEntities = new ArrayList<>();
        for (String hashStr : hashes) {
            Hash hashEntity = new Hash();
            hashEntity.setHash(hashStr);
            hashEntities.add(hashEntity);
        }
        hashRepository.saveAll(hashEntities);
    }
}