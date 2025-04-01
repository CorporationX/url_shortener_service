package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class Base62Encoder {
    private static final String HASH_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ALPHABET_LENGTH = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long number) {
        if (number == 0) {
            return String.valueOf(HASH_ALPHABET.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % ALPHABET_LENGTH);
            result.append(HASH_ALPHABET.charAt(remainder));
            number /= ALPHABET_LENGTH;
        }

        return result.toString();
    }
}
