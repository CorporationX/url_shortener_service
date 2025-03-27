package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.event.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range:1000}")
    private int maxRange;

    @Async("hashThreadPoolTaskExecutor")
    @Transactional
    public void generateBatch(){
        try {
            List<Long> numbers = hashRepository.getNextRange(maxRange);
            List<String> hashes = base62Encoder.encode(numbers);

            List<Hash> hashEntities = hashes.stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashEntities);
            log.info("Успешно сгенерированные и сохраненные хэши {}", hashEntities.size());
        } catch (Exception e) {
            log.error("Ошибка при генерации или сохранении хэшей: ", e);
        }
    }
}
