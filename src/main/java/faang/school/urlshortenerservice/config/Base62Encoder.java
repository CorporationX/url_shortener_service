package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Numbers list cannot be null or contain null values");
        }
        if (numbers.isEmpty()) {
            return Collections.emptyList();
        }

        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        if (number < 0) {
            throw new IllegalArgumentException(String.format("Number must be non-negative: %s", number));
        }
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            char digit = ALPHABET.charAt(remainder);
            result.append(digit);
            number /= BASE;
        }

        return result.toString();
    }
}