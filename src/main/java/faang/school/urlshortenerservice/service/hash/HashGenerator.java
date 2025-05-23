package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashConfig hashConfig;

    @Async("hashGeneratorThreadPool")
    public void generateHashesBatch() {
        log.info("Starting hash batch generation");
        int numberOfElements = hashConfig.getNumberOfElements();
        List<Long> numbers = hashRepository.getUniqueNumbers(numberOfElements);
        List<String> hashes = base62Encoder.encodeNumbers(numbers);
        hashRepository.saveAll(hashes);
        log.info("Successfully saved {} hashes", hashes.size());
    }
}