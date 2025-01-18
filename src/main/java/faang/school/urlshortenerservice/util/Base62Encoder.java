package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public String encode(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }
        return encoded.toString();
    }
}
