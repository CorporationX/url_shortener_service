package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.hash-generator.batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGenerationThreadPool")
    public void generateBatch() {
        List<Hash> hashEntities = base62Encoder.encode(hashRepository.getUniqueNumbers(batchSize))
                .stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashEntities);
        log.info("Генерация пакета хэшей завершена. Сохранено {} хэшей.", hashEntities.size());
    }
}
