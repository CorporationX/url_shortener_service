package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${generator-hash.number-hashes-batch}")
    private int numberOfBatch;

    @Async(value = "hashGeneratorThreadPool")
    public List<String> generateBatch() {
        List<Long> uniqueNumber = hashRepository.getUniqueNumbers(numberOfBatch);
        List<String> hashes = base62Encoder.encode(uniqueNumber);
        log.info("Create batch hashes. Batch size: {}", numberOfBatch);
        hashRepository.save(hashes);
        return hashes;
    }
}
