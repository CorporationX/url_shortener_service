package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;
    private static final int HASH_LENGTH = 6;
    private static final long MAX_VALUE = (long) Math.pow(BASE, HASH_LENGTH) - 1;

    public String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Value too large to encode in " + HASH_LENGTH + " base62 chars");
        }

        StringBuilder sb = new StringBuilder();

        if (value == 0) {
            sb.append('0');
        } else {
            while (value > 0) {
                int remainder = (int) (value % BASE);
                sb.append(BASE62.charAt(remainder));
                value /= BASE;
            }
        }

        while (sb.length() < HASH_LENGTH) {
            sb.append('0');
        }

        return sb.reverse().toString();
    }
}

