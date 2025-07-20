package faang.school.urlshortenerservice.util.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generate_batch_size}")
    private final int generateBatchSize;

    @Override
    @Async
    public void generateBatch() {
        log.info("Hashes Generation started");
        hashRepository.save(
                base62Encoder.encode(hashRepository.getUniqueNumbers(generateBatchSize)));
        log.info("Hashes was generated, from {} to {}",
                hashRepository.getUniqueNumbers(generateBatchSize),
                hashRepository.getUniqueNumbers(generateBatchSize).size() - 1);
    }
}