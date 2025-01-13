package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    @Value("${batch.size}")
    private int batchSize;

    @Async("customThreadPoolTaskExecutor")
    public void generateBatch() {
        List<Long> numbers = hashService.getUniqueNumbers(batchSize);
        List<HashEntity> hashes = base62Encoder.encode(numbers);

        hashService.saveHashes(hashes);

        log.info("Batch of {} hashes generated and saved.", hashes.size());
    }
}