package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.config.hashconfig.HashConfig;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashConfig hashConfig;

    public String generateHash() {

        long uniqueNumber = System.currentTimeMillis();


        String hash = base62Encoder.encode(uniqueNumber);


        if (hash.length() > 6) {
            hash = hash.substring(0, 6);
        } else {
            while (hash.length() < 6) {
                hash = "0" + hash;
            }
        }

        log.debug("Generated hash: {}", hash);
        return hash;
    }

    @Async("hashGeneratorTaskExecutor")
    public void generatedBatch() {
        try {
            log.info("Starting batch hash generation");

            long start = hashRepository.getNextSequenceValue(hashConfig.getBatchSize());
            List<Long> uniqueNumbers = LongStream.range(start, start + hashConfig.getBatchSize()).boxed().collect(Collectors.toList());

            List<String> hashes = uniqueNumbers.stream()
                    .map(base62Encoder::encode)
                    .map(hash -> {
                        if (hash.length() > 6) {
                            return hash.substring(0, 6);
                        } else {
                            while (hash.length() < 6) {
                                hash = "0" + hash;
                            }
                            return hash;
                        }
                    })
                    .collect(Collectors.toList());

            List<Hash> hashEntities = hashes.stream().map(hash -> Hash.builder().hash(hash).build()).collect(Collectors.toList());

            hashRepository.saveAll(hashEntities);

            log.info("Successfully generated and saved {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Failed to generate and save hashes: {}", e.getMessage(), e);
        }
    }
}