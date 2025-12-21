package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.batch.size:1000}")
    private int batchSize;

    @Scheduled(cron = "0 */5 * * * ?")
    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = encoder.encode(numbers);
        hashRepository.save(hashes);
    }
}