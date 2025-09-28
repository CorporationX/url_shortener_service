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

    private final HashRepository repository;
    private final Base62Encoder encoder;

    @Async("Executor")
    public void generateBatch() {
        List<Long> processedNumbers = repository.getUniqueNumbers(amountOfNumbers);
        List<Hash> generatedHashes = encoder.encode(processedNumbers).stream().map(hash -> new Hash()).toList();
        repository.saveAll(generatedHashes);
    }

    @Transactional
    public Collection<? extends String> getHashes(int n) {
        List<String> processedHashes = repository.getHashBatch(n);
        if (processedHashes.size() != n) {
            generateBatch();
            processedHashes.addAll(getHashes(n - processedHashes.size()));
        }
        return processedHashes;
    }
}
