package faang.school.urlshortenerservice.util;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeSingle)
                .toList();
    }

    private String encodeSingle(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return result.reverse().toString();
    }
}
