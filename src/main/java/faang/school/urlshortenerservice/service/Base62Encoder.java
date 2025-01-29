package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::encode).toList();
    }

    public String encode(Long number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Value must be positive");
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE62_ALPHABET.charAt((int)(number % BASE)));
            number /= BASE;
        }

        return result.reverse().toString();
    }

}
