package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.unique_number_seq.maxRange:10000}")
    private int uniqueNumberMaxRange;

    @Transactional
    @Async("hashGeneratorThreadPool")
    @Scheduled(cron = "${hash-generator.cron.generate-hashes-cron:0 0 0 * * *}")
    public void generateBatch() {
        List<String> hashes = base62Encoder.encode(hashRepository.getUniqueNumbers(uniqueNumberMaxRange));
        log.info("Got unique numbers and encoded in base 62. Quantity: {}. Time: {}", hashes.size(), LocalDateTime.now());

        hashRepository.saveHashes(hashes);
        log.info("Saved hashes in hash repository. Quantity: {}", hashes.size());
    }
}