package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${numbers.uniq.number:10000}")
    private int range;

    @Transactional
    @Override
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(range);
        List<Hash> hashes = base62Encoder.encode(numbers);
        hashRepository.saveAll(hashes);
    }
    @Transactional
    public List<String> getHashes(int cashSize) {
        List<Hash> hashes = hashRepository.getHashBatch(cashSize);
        if (hashes.size() < cashSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(cashSize - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async
    public CompletableFuture<List<String>> getHashesAsync(int cashSize) {
        return CompletableFuture.completedFuture(getHashes(cashSize));
    }


}
