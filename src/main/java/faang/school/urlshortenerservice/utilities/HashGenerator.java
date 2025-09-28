package faang.school.urlshortenerservice.utilities;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash-generation.get-numbers}")
    private int amountOfNumbers;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("Executor")
    public void generateBatch() {
        List<Long> processedNumbers = hashRepository.getUniqueNumbers(amountOfNumbers);
        List<Hash> generatedHashes = base62Encoder.encode(processedNumbers).stream().map(hash -> new Hash()).toList();
        hashRepository.saveAll(generatedHashes);
    }

    @Transactional
    public Collection<? extends Hash> getHashes(int n) {
        List<Hash> processedHashes = hashRepository.getHashBatch(n);
        if (processedHashes.size() != n) {
            generateBatch();
            processedHashes.addAll(getHashes(n - processedHashes.size()));
        }
        return processedHashes;
    }
}
