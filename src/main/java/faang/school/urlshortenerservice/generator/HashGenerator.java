package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorProperties properties;

    @Transactional
    public void generateHash() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(properties.getBatchSize());
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        //uniqueNumbers.forEach();
    }
}
