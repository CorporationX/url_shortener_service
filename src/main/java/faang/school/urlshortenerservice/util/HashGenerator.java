package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    @Value("${hash-generation.get-numbers}")
    private int uniqueNumbersAmount;

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
        List<Hash> result = new ArrayList<>();

        while (result.size() < amount) {
            List<Hash> fetched = hashRepo.findAndDelete(amount - result.size());
            result.addAll(fetched);

            if (result.size() < amount) {
                log.info("Not enough hashes in DB, generating more...");
                generateBatch();
            }
        }
        return result;
    }
}
