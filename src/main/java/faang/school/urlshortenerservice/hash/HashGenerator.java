package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.generated-batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Async("customThreadPool")
    public void generateBatch() {
        List<Long> nums = hashRepository.getUniqueNumbers(batchSize);

        List<String> hashes = encoder.encode(nums);
        hashRepository.save(hashes);
    }
}
