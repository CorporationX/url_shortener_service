package faang.school.urlshortenerservice.util.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private final Executor taskExecutor;

    @Value("${hash.encoder.hash-size}")
    private int hashSize;

    @Value("${hash.encoder.characterBase62}")
    private String characterBase62;

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
            result.append(characterBase62.charAt((int) (number % characterBase62.length())));
            number /= characterBase62.length();
        }
        return result.toString();
    }
}
