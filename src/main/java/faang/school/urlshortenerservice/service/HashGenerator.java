package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.BaseEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${hash.generator.batch-size:1000}")
    private long batchSize;

    @Async("hashGeneratorExecutor")
    @Scheduled(cron = "${hash.generator.cron:0*****}")
    public void generateBatch(){
        log.info("Generating hashes");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = baseEncoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
        log.info("Saved {} hashes", hashes.size());
    }

    @Transactional
    public List<String> getHashes(long amount) {
        log.info("Getting {} hashes", amount);
        List<String> hashes = hashRepository.getHashBatchAndDelete(amount);
        if (hashes.size() < amount) {
            log.info("Not enough hashes, generating more");
            generateBatch();
            hashes.addAll(hashRepository.getHashBatchAndDelete(amount- hashes.size()));
            log.info("Generated {} hashes", hashes.size());
        }
        return hashes;
    }
}
