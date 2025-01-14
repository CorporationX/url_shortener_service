package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int base = alphabet.length();

    public String encode(Long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder hash = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % base);
            hash.insert(0, alphabet.charAt(remainder));
            number /= base;
        }

        return hash.toString();
    }
}
