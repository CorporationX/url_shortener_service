package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.urlshortenerservice.exception.ErrorMessages.HASH_GENERATION_FAILED;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${shortener.hash.reserve.batch-size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);

            if (numbers.isEmpty()) {
                log.error(HASH_GENERATION_FAILED);
                throw new HashGenerationException(HASH_GENERATION_FAILED);
            }

            List<String> hashes = encoder.encode(numbers);
            hashRepository.save(hashes);
            log.info("Generated and saved {} hashes", hashes.size());
        } catch (Exception e) {
            log.error(HASH_GENERATION_FAILED, e);
            throw new HashGenerationException(HASH_GENERATION_FAILED);
        }
    }
}
