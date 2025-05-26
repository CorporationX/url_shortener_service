package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    public static final int HASH_LENGTH = 6;
    public static final long MAX_VALUE = (long) Math.pow(BASE, HASH_LENGTH) - 1;

    public List<String> encode(List<Long> numbers) {
        Objects.requireNonNull(numbers, "Numbers list cannot be null");
        List<String> result = new ArrayList<>(numbers.size());
        StringBuilder sb = new StringBuilder(HASH_LENGTH);

        for (Long number : numbers) {
            result.add(encodeSingle(number, sb));
        }
        return result;
    }

    private String encodeSingle(long value, StringBuilder sb) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Value too large to encode in " + HASH_LENGTH + " base62 chars");
        }

        sb.setLength(0);

        if (value == 0) {
            sb.append("0".repeat(HASH_LENGTH));
            return sb.toString();
        }

        while (value > 0) {
            int remainder = (int) (value % BASE);
            sb.append(BASE62.charAt(remainder));
            value /= BASE;
        }

        while (sb.length() < HASH_LENGTH) {
            sb.append(BASE62.charAt(0));
        }

        return sb.reverse().toString();
    }
}