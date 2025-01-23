package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.service.hash.encoder.Encoder;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Encoder encoder;

    @Value("${hash.batch-size}")
    private int batchSize;

    @Value("${hash.encoding-batch-size}")
    private int encodingBatchSize;

    @Value("${hash.length}")
    private int hashLength;

    @Autowired
    @Qualifier("hashesThreadPool")
    private ExecutorService pool;

    @Async("hashesThreadPool")
    public CompletableFuture<Void> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<CompletableFuture<List<String>>> futures = ListUtils
                .partition(uniqueNumbers, encodingBatchSize)
                .stream()
                .map(values -> CompletableFuture.supplyAsync(() -> encoder.encode(values, hashLength), pool))
                .toList();
        CompletableFuture.allOf(new CompletableFuture[0]).join();
        List<String> combinedResults = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
        hashRepository.save(combinedResults);
        return CompletableFuture.completedFuture(null);
    }
}
