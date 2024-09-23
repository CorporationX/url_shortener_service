package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.db.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    @Value("${app.hash-generator.batch-size:10000}")
    private int batchSize;
    @Value("${app.hash-generator.minimum-size:5000}")
    private int minimumSize;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateBatchIfNeeded() {
        if (hashRepository.getHashesNumber() < minimumSize) {
            log.info("Hashes number is under minimum amount [{}], refilling DB", minimumSize);

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            hashRepository.saveBatch(hashes);
        }
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatchIfNeededAsync() {
        generateBatchIfNeeded();
    }

    @Transactional
    public List<String> getHashes(int numberToRefill) {
        return hashRepository.pollHashBatch(numberToRefill);
    }
}
