package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateBatch(int count) {
        List<Long> uniqueNumbers = hashRepository.getUniqueValue(count);
        List<String> hashes = base62Encoder.encodeListOfNumbers(uniqueNumbers);
        hashRepository.save(hashes);
        log.info("Hashes saved to database");
    }
}
