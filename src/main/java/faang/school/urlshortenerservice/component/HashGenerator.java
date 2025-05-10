package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.config.app.HashConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashConfig hashConfig;

    @Async("hashGeneratorTaskExecutor")
    public void generateBatch() {
        int batchSize = hashConfig.getBatchSize();
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);
    }
}
