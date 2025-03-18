package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.unique-numbers-amount:1000}")
    private int uniqueNumbersCount;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumbersCount);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
    }
}
