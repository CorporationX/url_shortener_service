package faang.school.urlshortenerservice.ñache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${queue-size}")
    private int queueSize;
    @Value("${percent}")
    private int percent;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    public BlockingQueue<String> hashes() {
        return new ArrayBlockingQueue<>(queueSize);
    }

    @Async("batchExecutor")
    public String getHash() {
        int size = hashes().size();
        int threshold = percent * size;
        if (size < threshold) {
            List<String> hashBatch = hashRepository.getHashBatch(queueSize);
            for (String batch : hashBatch) {
                hashes().add(batch);
            }
            hashGenerator.generateBatch();
        }
        return hashes().peek();
    }
}
