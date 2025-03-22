package faang.school.urlshortenerservice.service.hash;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${hash.generate.thread-count}")
    private int threadPoolSize;

    private final Executor executor;

    public Base62Encoder(@Qualifier("generateHashExecutor") Executor executor) {
        this.executor = executor;
    }

    public CompletableFuture<List<String>> encode(List<Long> numbers) {
        List<CompletableFuture<List<String>>> futures = splitList(numbers).stream()
                .map(subList -> CompletableFuture.supplyAsync(() -> encodeBatch(subList), executor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .flatMap(future -> future.join().stream())
                        .collect(Collectors.toList()));
    }

    private List<List<Long>> splitList(List<Long> list) {
        int blockSize = (int) Math.ceil((double) list.size() / threadPoolSize);
        int totalBlocks = (int) Math.ceil((double) list.size() / (double) blockSize);
        List<List<Long>> result = new ArrayList<>();


        for (int i = 0; i < totalBlocks; i++) {
            int start = i * blockSize;
            int end = Math.min(start + blockSize, list.size());
            if (start < end) {
                result.add(list.subList(start, end));
            }
        }

        return result;
    }

    private List<String> encodeBatch(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeOne)
                .collect(Collectors.toList());
    }

    private String encodeOne(Long number) {
        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            sb.append(BASE62_CHARS.charAt((int) (number % 62)));
            number /= 62;
        }

        while (sb.length() < 6) {
            sb.append('0');
        }

        return sb.reverse().substring(0,6);
    }
}
