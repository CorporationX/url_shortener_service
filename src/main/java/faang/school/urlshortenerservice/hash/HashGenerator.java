package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import faang.school.urlshortenerservice.repository.HashRepositoryJdbcImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepositoryJdbcImpl hashRepository;
    private final Base62Encoder encoder;
    private final ConstantsProperties constantsProperties;

    private Long generatorThreshold;

    @PostConstruct
    private void init() {
        generatorThreshold =
                Math.max(10L * constantsProperties.getGenerationThresholdPercent(),
                        constantsProperties.getLocalCachingSize());
    }

    @Async("taskExecutor")
    public void generateBatch() {
        if (hashRepository.countHashes() > generatorThreshold) return;
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(constantsProperties.getGenerationBathSize());
        List<String> newHashes = encoder.encode(uniqueNumbers);
        hashRepository.save(newHashes);
    }
}
