package faang.school.urlshortenerservice.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import faang.school.urlshortenerservice.config.MainConfig;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final MainConfig mainCofig;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    public List<Hash> generateBatch() {
        log.info("The thread {}.", Thread.currentThread().getName());
        log.info("Generating hashes... for {} numbers.", mainCofig.getNumberOfNumbers());

        List<Long> uniqueNumbers = hashRepository.getNextNumBachOf(mainCofig.getNumberOfNumbers());
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        List<Hash> generatedHashes = new ArrayList<>();

        for (String hash : hashes) {
            generatedHashes.add(new Hash(hash));
        }
        return hashRepository.saveAll(generatedHashes);
    }
}
