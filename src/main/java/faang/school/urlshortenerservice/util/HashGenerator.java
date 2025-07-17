package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash-generation.get-numbers")
    private final int uniqueNumbersAmount;

    private final HashRepository hashRepo;
    private final Base62Encoder encoder;

    @Async("Executor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepo.getUniqueNumbers(uniqueNumbersAmount);
        List<Hash> hashes = encoder.encode(uniqueNumbers).map(Hash::new).toList();
        hashRepo.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = hashRepo.findAndDelete(amount);
        if (hashes.size() != amount) {
            generateBatch();
            hashes.addAll(getHashes(amount - hashes.size()));
        }
        return hashes;
    }
}
