package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch}")
    private int batch;

    @Async("hashCacheExecutor")
    public void generateBatch() {
        log.debug("Generating batch of {} hashes", batch);
        List<Long> numbers = hashRepository.getUniqueNumbers(batch);
        List<String> hashes = base62Encoder.encodeBatch(numbers);
        hashRepository.save(hashes);
    }
}
