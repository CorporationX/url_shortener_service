package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            result.append(ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }

        return result.toString();
    }
}