package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import faang.school.urlshortenerservice.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashCacheQueueProperties properties;
    private final HashRepositoryImpl hashRepository;
    private final Base62Encoder base62Encoder;
    private final ThreadPool threadPool;
    private final Util util;

    @Transactional
    @Async(value = "hashGeneratorExecutor")
    public void generateBatchHashes(int batchSize) {
        log.info("Start generating {} hashes", batchSize);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<List<Long>> batches = util.getBatches(uniqueNumbers, properties.getFillingBatchesQuantity());

        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        batches.forEach(batch -> futures.add(CompletableFuture.supplyAsync(() ->
                base62Encoder.encode(batch), threadPool.hashGeneratorExecutor()
        )));

        List<String> hashes = new ArrayList<>();
        futures.forEach(future -> hashes.addAll(future.join()));
        hashRepository.saveHashes(hashes);
        log.info("Finished generating {} hashes", batchSize);
    }
}
