package faang.school.urlshortenerservice.config.hashegeneration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    @Value("${spring.base62.base62-alphabet}")
    private String base62Alphabet;
    @Value("${spring.base62.base}")
    private int base;

    public String encode(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number != 0) {
            int remainder = (int) (number % base);
            encoded.append(base62Alphabet.charAt(remainder));
            number /= base;
        }
        return encoded.reverse().toString();
    }
}
