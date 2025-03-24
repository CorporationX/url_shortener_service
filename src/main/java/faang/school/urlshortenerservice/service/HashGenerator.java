package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorPool")
    public void generateBatch(int fetchSize) {
        List<Long> sequence = hashRepository.getUniqueNumbers(fetchSize);
        List<String> hashes = base62Encoder.generateHashes(sequence);
        hashRepository.saveAll(hashes);
    }
}
