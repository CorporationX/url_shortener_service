package faang.school.urlshortenerservice.utilities;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash-generation.get-numbers}")
    private int numbersAmount;

    private final HashRepository repository;
    private final Base62Encoder encoder;

    @Async("Executor")
    public void generateBatch() {
        List<Long> numbers = repository.getUniqueNumbers(numbersAmount);
        List<Hash> hashList = encoder.encode(numbers).stream().map(hash -> new Hash()).toList();
        repository.saveAll(hashList);
    }

    @Transactional
    public Collection<? extends String> getHashes(int n) {
        List<String> hashList = repository.getHashBatch(n);
        if (hashList.size() != n) {
            generateBatch();
            hashList.addAll(getHashes(n - hashList.size()));
        }
        return hashList;
    }
}
