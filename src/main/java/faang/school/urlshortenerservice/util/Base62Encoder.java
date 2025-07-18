package faang.school.urlshortenerservice.util;

import java.util.List;

public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

    public static List<String> encode(List<Long> numbers) {
        return numbers.parallelStream()
                .map(Base62Encoder::encodeSingle)
                .toList();
    }

    private static String encodeSingle(long number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }
        StringBuilder sb = new StringBuilder();
        long num = number;
        while (num > 0) {
            sb.append(BASE62_CHARS.charAt((int) (num % BASE)));
            num /= BASE;
        }
        return sb.reverse().toString();
    }
}
