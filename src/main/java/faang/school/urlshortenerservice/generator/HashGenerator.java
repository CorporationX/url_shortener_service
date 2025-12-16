package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final UrlShortenerConfig urlShortenerConfig;

    @Async("urlShortenerExecutor")
    public void generateBatch() {
        try {
            List<Long> longs = hashRepository.getUniqueNumbers(urlShortenerConfig.getNumberCount());
            List<String> hashes = base62Encoder.encode(longs);
            hashRepository.save(hashes);
        } catch (Exception exception) {
            log.error("Failed to generate hash batch: {}", exception.getMessage(), exception);
        }
    }
}
