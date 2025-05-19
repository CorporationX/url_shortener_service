package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    public static final int HASH_LENGTH = 6;
    public static final long MAX_VALUE = (long) Math.pow(BASE, HASH_LENGTH) - 1;

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            result.add(encodeSingle(number));
        }
        return result;
    }

    private String encodeSingle(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Value too large to encode in " + HASH_LENGTH + " base62 chars");
        }

        StringBuilder sb = new StringBuilder();
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


