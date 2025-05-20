package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encodeBatch(List<Long> numbers) {

        return numbers.stream()
                .parallel()
                .map(this::base62Encode)
                .toList();
    }

    private String base62Encode(long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int digit = (int) (number % BASE);
            result.append(BASE62_ALPHABET.charAt(digit));
            number /= BASE;
        }
        return result.reverse().toString();
    }
}
