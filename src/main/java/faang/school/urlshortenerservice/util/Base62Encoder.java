package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private final String alphabet;
    private final int base;

    public Base62Encoder(@Value("${hash.generator.alphabet}") String alphabet) {
        if (alphabet == null || alphabet.isBlank()) {
            throw new IllegalArgumentException("Alphabet must not be null or empty");
        }
        this.alphabet = alphabet;
        this.base = alphabet.length();
    }

    public String encode(long number) {
        if (number == 0) return String.valueOf(alphabet.charAt(0));

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(alphabet.charAt((int) (number % base)));
            number /= base;
        }
        return sb.reverse().toString();
    }
}