package faang.school.urlshortenerservice.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    @Value("${url-shortener-service.base62}")
    private final String BASE62_CHARS;
    @Value("${url-shortener-service.batch-size}")
    private final int BATCH_SIZE;

    public List<String> encode(List<Long> numbers) {
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();
        int len = numbers.size();

        for (int i = 0; i < len; i += BATCH_SIZE) {
            List<Long> batch = numbers.subList(i, Math.min(i + BATCH_SIZE, len));
            CompletableFuture<List<String>> future = createFuture(batch);
            futures.add(future);
        }

        return runThreads(futures);
    }

    private CompletableFuture<List<String>> createFuture(List<Long> batch) {
        return CompletableFuture.supplyAsync(() ->
                batch.stream()
                        .map(this::encodeBase62)
                        .collect(Collectors.toList())
        );
    }

    private List<String> runThreads(List<CompletableFuture<List<String>>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(thread -> futures.stream()
                        .flatMap(newList -> newList.join().stream())
                        .collect(Collectors.toList()))
                .join();
    }

    private String encodeBase62(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (number % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            number /= 62;
        } while (number > 0);
        return sb.reverse().toString();
    }
}
