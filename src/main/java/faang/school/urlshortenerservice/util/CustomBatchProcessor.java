package faang.school.urlshortenerservice.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CustomBatchProcessor<T> implements BatchProcessor<T> {

    private final ThreadPoolTaskExecutor hashBatchProcessingExecutor;
    private final int batchDivider;


    public CustomBatchProcessor(ThreadPoolTaskExecutor hashBatchProcessingExecutor, int batchDivider) {
        this.hashBatchProcessingExecutor = hashBatchProcessingExecutor;
        this.batchDivider = batchDivider;
    }

    @Override
    public void processBatches(List<T> source, Consumer<List<T>> task) {
        int numberOfItems = source.size();
        int batchSize = Math.max(batchDivider, numberOfItems / batchDivider);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i += batchSize) {
            List<T> batch = source.subList(i, Math.min(i + batchSize, numberOfItems));
            CompletableFuture<Void> future = CompletableFuture
                    .runAsync(() -> task.accept(batch), hashBatchProcessingExecutor);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}