package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
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

    @Value("${hash.generate-batch}")
    private int generateBatch;

    @Async("generateExecutorService")
    public void generateBatch() {
        log.info("Starting generating hashes");
        List<Long> numbers = hashRepository.getUniqueNumbers(generateBatch);
        log.debug("Get unique numbers from sequence");
        List<Hash> hashes = base62Encoder.encode(numbers)
                .stream()
                .map(Hash::new)
                .toList();
        log.debug("Encoded numbers to hashes");
        hashRepository.saveAll(hashes);
        log.info("Finished generating hashes");
    }
}