package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${batchSize}")
    private long batchSize;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Async("async")
    public void generateBatch() {
        List<Long> forHashGenerate = hashRepository.getUniqueNumbers(batchSize);
        System.out.println(Thread.currentThread().getName() + "ТУТ ИЗ ГЕНЕРАЦИИ БАОТЧЕЙ");
        List<Hash> hashes = base62Encoder.encode(forHashGenerate);
        hashRepository.saveAll(hashes);

    }
}
