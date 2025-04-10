package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private final String alphabet;
    private final int base;

    @Autowired
    public Base62Encoder(@Value("${encoder.base62.alphabet}") String alphabet) {
        this.alphabet = alphabet;
        this.base = alphabet.length();
    }

    public String encode(Long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder hash = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % base);
            hash.append(alphabet.charAt(remainder));
            number /= base;
        }

        return hash.toString();
    }

}
