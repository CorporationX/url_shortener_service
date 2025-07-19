package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Async("hashExecutor")
    public void generateBatch() {
        log.info("HashGenerator: starting batch of {} hashes", batchSize);

        List<Long> ids = hashRepository.getUniqueNumbers(batchSize);

        List<String> hashes = ids.stream()
                .map(base62Encoder::encode)
                .toList();

        hashRepository.saveAll(
                hashes.stream()
                        .map(hash -> new Hash(hash))
                        .toList()
        );
        log.info("HashGenerator: saved {} new hashes", hashes.size());
    }
}
