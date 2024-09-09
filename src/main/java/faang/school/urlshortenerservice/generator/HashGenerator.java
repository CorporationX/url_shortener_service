package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${spring.generator.count_number}")
    private int countGenerateNumber;

    @Async("executorService")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(countGenerateNumber);
        List<Hash> hashes = base62Encoder.encodeNums(uniqueNumbers);

        hashRepository.saveAll(hashes);
    }
}
