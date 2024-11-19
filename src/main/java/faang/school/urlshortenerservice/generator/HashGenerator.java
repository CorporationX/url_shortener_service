package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${params.count}")
    private long count;

    @Async("hashGeneratorExecutor")
    public void generateHash() {
        log.info("Starting hash generation for {} unique numbers.", count);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
        log.info("Successfully saved {} hashes to the repository.", hashes.size());
    }

    @PostConstruct
    public void init() {
        generateHash();
    }
}
