package faang.school.urlshortenerservice.utils;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62Encoder {
    @Value("${thread_pool.encode.pool_size:5}")
    private int pool_size;
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final ExecutorService encodeThreadPool;

    public List<String> encode(@NotNull List<Long> numbers) {
        int batch_size = numbers.size() / pool_size;
        List<Future<List<String>>> futures = new ArrayList<>();

        IntStream.range(0, pool_size)
                .forEach(i -> {
                    int start = i * batch_size;
                    int end = (i + 1) * batch_size;
                    futures.add(encodeBatch(numbers.subList(start, end)));
                });

        List<String> hashes = new ArrayList<>();
        futures.forEach(future -> {
            try {
                hashes.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error occurred", e);
            }
        });

        encodeThreadPool.shutdown();

        return hashes;
    }

    private Future<List<String>> encodeBatch(@NotNull List<Long> batch) {
        return encodeThreadPool.submit(() ->
                batch.stream()
                        .map(this::encodeNumber)
                        .toList());
    }

    private String encodeNumber(long number) {
        int base = BASE62.length();
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base);
            result.append(BASE62.charAt(remainder));
            number /= base;
        }
        return result.toString();
    }
}
