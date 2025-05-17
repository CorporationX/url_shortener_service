package faang.school.urlshortenerservice.andreev.service.generator;

import faang.school.urlshortenerservice.andreev.encoder.Base62Encoder;
import faang.school.urlshortenerservice.andreev.exception.HashGenerationException;
import faang.school.urlshortenerservice.andreev.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.urlshortenerservice.andreev.exception.ErrorMessage.HASH_GENERATION_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {
    private static final String INFO_GENERATE_BATCH = "Generated and saved {} hashes";

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${shortener.hash.batch.size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encode(numbers);
            hashRepository.save(hashes);
            log.info(INFO_GENERATE_BATCH, hashes.size());
        } catch (Exception e) {
            log.error(HASH_GENERATION_FAILED, e);
            throw new HashGenerationException(HASH_GENERATION_FAILED);
        }
    }
}
