package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long seed) {
        StringBuilder result = new StringBuilder();
        while (seed > 0) {
            result.append(ALPHABET.charAt((int) (seed % ALPHABET.length())));
            seed /= ALPHABET.length();
        }

        return result.toString();
    }
}
