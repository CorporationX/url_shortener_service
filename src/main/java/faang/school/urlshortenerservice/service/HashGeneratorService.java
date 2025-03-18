package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGeneratorService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash_generator.db_hashes_limit}")
    private long dbHashesLimit;

    @Value("${app.hash_generator.initial_hashes}")
    private long initialHashes;

    @PostConstruct
    public void generateOnStartup() {
        log.info("Checking and generating initial hashes...");
        generate(initialHashes);
    }

    @Scheduled(fixedRateString = "${app.hash_generator.fixed_rate}")
    public void generateIfNeeded() {
        long existingHashes = hashRepository.count();
        if (existingHashes < dbHashesLimit) {
            long hashesToGenerate = dbHashesLimit - existingHashes;
            generate(hashesToGenerate);
        } else {
            log.info("There are enough hashes, no generation needed.");
        }
    }

    public void generate(long count) {
        log.info("Generating {} new hashes...", count);

        List<Long> numbers = hashRepository.getUniqueNumbers(count);
        log.info("Generated numbers: {}", numbers);
        List<String> hashes = base62Encoder.encode(numbers);

        List<Hash> hashEntities = hashes.stream().map(Hash::new).collect(Collectors.toList());
        hashRepository.saveAll(hashEntities);

        log.info("{} new hashes successfully generated and saved!", hashEntities.size());
    }
}