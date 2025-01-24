package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch-size:1000000}")
    private long batchSize;

    @Transactional
    public List<String> getHashList(long quantity) {
        log.info("Start getting hashes from the repository.");
        List<String> hashList = hashRepository.getHashesAndDelete(quantity);

        log.info("Retrieved {} hashes from the repository: {}", hashList.size(), hashList);

        if (hashList.size() < quantity) {
            log.info("Not enough hashes (size = {}), generating more", hashList.size());
            generateHashList();

            List<String> generatedHashList = hashRepository.getHashesAndDelete(quantity - hashList.size());

            hashList.addAll(generatedHashList);
        }
        log.info("Finish getting hashes from repository: {}", hashList.size());

        return hashList;
    }

    @Async("hashGeneratorTaskExecutor")
    @Transactional
    public void generateHashList() {
        List<Long> range = hashRepository.getNextRangeHashes(batchSize);

        log.info("Start generate {} hashes for range: {}", batchSize, range);
        List<Hash> hashList = base62Encoder.encode(range).stream()
                .map(Hash::new)
                .toList();
        log.info("End generate {} hashes for range: {}", batchSize, range);

        hashRepository.saveAll(hashList);
        log.info("Hashes saved, size = {}.", hashList.size());
    }
}
