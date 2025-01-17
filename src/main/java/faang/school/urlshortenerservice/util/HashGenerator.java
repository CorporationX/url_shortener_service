package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashService hashService;
    private final BaseEncoder baseEncoder;

    @Value("${batch-properties.hash-generate-batch}")
    private Long batchSize;

    @Async("asyncThreadPool")
    public void generateHashBatch(Long batchSize) {
        List<Long> uniqueNumbers = hashService.getUniqueSeqNumbers(batchSize);
        hashService.saveHashes(baseEncoder.encodeList(uniqueNumbers));
        log.info("Created and saved {} hashes", uniqueNumbers.size());
    }
}
