package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class Base62Encoder {
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = ALPHABET.length;

    public List<String> encodeBatch(List<Long> numbers) {
        if (numbers == null) {
            return Collections.emptyList();
        }

        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long num) {
        if (num == 0) {
            return String.valueOf(ALPHABET[0]);
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(ALPHABET[(int) (num % BASE)]);
            num /= BASE;
        }
        return sb.reverse().toString();
    }
}
