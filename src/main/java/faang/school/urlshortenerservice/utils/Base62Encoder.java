package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long value) {
        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % BASE62_ALPHABET.length());
            encoded.append(BASE62_ALPHABET.charAt(remainder));
            value /= BASE62_ALPHABET.length();
        }
        return encoded.reverse().toString();
    }
}
