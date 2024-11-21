package faang.school.urlshortenerservice.util.encoder;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class Base62Encoder implements Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final Executor taskExecutor;

    @Setter
    @Value("${hash.encoder.hash-size}")
    private int hashSize;

    public List<String> encodeBatch(List<Long> sequence) {
        List<CompletableFuture<String>> futures = sequence.stream()
                .map(num -> CompletableFuture.supplyAsync(() -> encode(num), taskExecutor))
                .toList();
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private String encode(Long number) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hashSize; i++) {
            result.append(ALPHABET.charAt((int) (number % ALPHABET.length())));
            number /= ALPHABET.length();
        }
        return result.toString();
    }
}
