package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseConversion;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
@Component
public class HashGenerator {

    @Value("${app.hash-batch-size}}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final BaseConversion baseConversion;

    //TODO: try-catch возможно нужно будет сюда добавить
    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<Void> generateBatch() {
        log.info("Starting batch generation with batch size: {}", batchSize);
        List<Long> uniqueNumbers = hashRepository.getFollowingRangeUniqueNumbers(batchSize);
        List<Hash> hashes = baseConversion.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        return CompletableFuture.completedFuture(null);
    }

}