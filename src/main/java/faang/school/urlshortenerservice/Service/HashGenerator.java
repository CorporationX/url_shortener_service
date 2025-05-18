package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashProperties properties;
    private final Base62Encoder encoder;

    @Async("hash-generator-pool")
    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(properties.batchsize());
        List<String> hashes = encoder.encode(numbers);
        log.info("Generated {} hashes", hashes.size());
        hashRepository.saveHashesByBatch(hashes);
        log.info("Saved hashes: {} ", hashes);
    }


}
