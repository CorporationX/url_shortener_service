package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashBatchGenerator hashBatchGenerator;

    @Transactional
    public List<String> getHashes(long amount) {
        log.info("Generating {} hashes", amount);
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            hashBatchGenerator.generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }
}
