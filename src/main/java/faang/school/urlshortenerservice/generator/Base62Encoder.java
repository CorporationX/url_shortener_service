package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final ExecutorService urlPool;

    public List<String> encode(List<Long> numbers) {
        log.info("Starting encode of " + numbers.size() + " numbers");
        return CompletableFuture.supplyAsync(() -> numbers.stream().map(this::generateBase62Characters).toList(), urlPool).join();
    }

    private String generateBase62Characters(long number) {
        StringBuilder str = new StringBuilder();
        if (str.length() <= 6) {
            while (number > 0) {
                str.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
                number /= BASE_62_CHARACTERS.length();
            }
            return str.toString();
        } else {
            log.error("number {} is too big, because hash is {} length", number, str.length());
            throw new RuntimeException("Number is too big");
        }
    }
}
