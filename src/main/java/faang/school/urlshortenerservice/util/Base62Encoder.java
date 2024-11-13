package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return result.reverse().toString();
    }
}
