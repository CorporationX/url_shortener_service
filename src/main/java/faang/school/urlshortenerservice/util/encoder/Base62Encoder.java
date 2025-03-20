package faang.school.urlshortenerservice.util.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();
    private final ExecutorService base62EncodingExecutorService;

    public String encode(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.insert(0, BASE62.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return encoded.toString();
    }

    public List<String> encode(List<Long> numbers) {
        List<CompletableFuture<String>> futures = numbers.stream()
                .map(n -> CompletableFuture.supplyAsync(
                        () -> encode(n),
                        base62EncodingExecutorService
                ))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
