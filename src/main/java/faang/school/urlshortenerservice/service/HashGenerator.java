package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepositoryJdbc hashRepositoryJdbc;
    private final Base62Encoder base62Encoder;

    @Value("${url.hash.batch-size}")
    private int hashBatchSize;

    @Async("hashGenPool")
    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepositoryJdbc.getUniqueNumbers(hashBatchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepositoryJdbc.save(hashes);
    }
}
