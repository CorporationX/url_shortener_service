package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final String base62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int baseLength = base62.length();
    private static final int CODE_LENGTH = 6;

    public List<String> encode(List<Long> value) {
        return value.stream()
                .map(this::generateHash)
                .toList();
    }

    private String generateHash(Long value) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Value must be a non-negative number");
        }
        StringBuilder hash = new StringBuilder();

        while (value > 0) {
            int remainder = (int) (value % 62);
            hash.append(base62.charAt(remainder));
            value = value / baseLength;
        }

        while (hash.length() < CODE_LENGTH) {
            hash.append(base62.charAt(0));
        }
        return hash.reverse().toString();
    }
}