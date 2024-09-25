package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.unique_numbers}")
    private int uniqueNumbers;
    @Value("${hash.daily_batch_size}")
    private int dailySizeHashBatch;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(uniqueNumbers);
        List<String> newHash = base62Encoder.encode(numbers);
        List<Hash> result = newHash.stream()
                .map(hash -> Hash.builder()
                        .hash(hash)
                        .build())
                .toList();
        hashRepository.saveAll(result);
    }

    @Scheduled(cron = "${scheduled.generate_batch}")
    @Transactional(readOnly = true)
    public void checkSizeHashBatch() {
        if (dailySizeHashBatch > hashRepository.count()) {
            generateBatch();
        }
    }

}
