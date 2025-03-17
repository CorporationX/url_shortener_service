package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${batch.generation-size}")
    private int GENERATION_BATCH_SIZE;

    @Async(value = "threadPoolExecutor")
    @Transactional
    public List<Hash> generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(GENERATION_BATCH_SIZE);
        List<String> hashes = base62Encoder.encodeBatch(numbers);
        List<Hash> mapped =  hashes.stream()
                .map(Hash::new)
                .toList();
        log.info("Generated {} hashes", mapped);
        List<Hash> saved = hashRepository.saveAll(mapped);
        log.info("Generated and saved {} hashes.", saved.size());
        return saved;
    }

}
