package faang.school.urlshortenerservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UtilityClass
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 6;

    public static List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(Base62Encoder::encodeBase62)
                .toList();
    }

    private String encodeBase62(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(BASE62_CHARS.charAt(remainder));
            number /= BASE;
        }

        sb.reverse();

        while (sb.length() < MIN_LENGTH) {
            sb.insert(0, '0');
        }

        if (sb.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Encoded Base62 string exceeds 6 characters — input number too large");
        }

        return sb.toString();
    }
}
