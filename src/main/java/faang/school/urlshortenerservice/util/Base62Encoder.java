package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final StringBuilder builder = new StringBuilder();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::applyEncoding)
                .toList();
    }

    private String applyEncoding(long number) {
        while (number > 0) {
            builder.append(BASE62_ALPHABET.charAt((int) (number % BASE62_ALPHABET.length())));
            number /= BASE62_ALPHABET.length();
        }
        return builder.toString();
    }
}
