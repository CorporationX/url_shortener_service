package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGeneratorService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.batch-size:50}")
    private int batchSize;

    @Transactional
    public void generateBatch() {
        log.info("Generating batch of {} hashes", batchSize);
        List<Long> randomNumbersList = hashRepository.getUniqueNumbers(batchSize);

        if (randomNumbersList.isEmpty()) {
            log.warn("No free numbers available for generating new hashes");
            return;
        }

        List<String> hashList = hashGenerator.generateBatch(randomNumbersList);
        hashRepository.saveAll(hashList);
        log.info("Saved {} new hashes to database", hashList.size());
    }

    @Transactional(readOnly = true)
    public int getAvailableHashesCount() {
        return hashRepository.countAvailableHashes();
    }
}
