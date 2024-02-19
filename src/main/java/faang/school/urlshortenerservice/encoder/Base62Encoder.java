package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    @Value("${base62.alphabet}")
    private String alphabet;

    @Value("${base62.batch_size}")
    private int batchSize;

    private final ExecutorService executor;

    public List<Hash> encode(List<Long> numbers) {

        List<CompletableFuture<List<Hash>>> futures = new ArrayList<>();

        for (int i = 0; i < numbers.size(); i += batchSize) {
            int end = Math.min(numbers.size(), i + batchSize);

            List<Long> batch = numbers.subList(i, end);

            CompletableFuture<List<Hash>> future = CompletableFuture.supplyAsync(() -> batch.stream()
                    .map(this::decimalToBase62)
                    .toList(), executor);

            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(f -> futures.stream()
                        .flatMap(x -> x.join().stream()))
                .join()
                .toList();
    }

    private Hash decimalToBase62(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Decimal number cannot be negative");
        }

        StringBuilder base62 = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % alphabet.length());
            base62.append(alphabet.charAt(remainder));
            number /= alphabet.length();
        }

        return new Hash(base62.reverse().toString());
    }
}
