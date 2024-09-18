package faang.school.urlshortenerservice.config.hashegeneration;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder encoder;

    @Async("generateHashExecutor")
    public void generateBatch() {
        List<Long> numbers = hashService.getUniqueNumbers();
        List<String> hashes = numbers.stream().map(encoder::encode).toList();
        hashService.save(hashes);
    }

    @Async("generateHashExecutor")
    public void fillQueueAsync(LinkedBlockingQueue<String> queue, List<String> hashes) {
        while (queue.size() < queue.remainingCapacity() && !hashes.isEmpty()) {
            String value = hashes.remove(0);
            queue.add(value);
        }
        if (!hashes.isEmpty()) {
            hashService.save(hashes);
        }
    }
}
