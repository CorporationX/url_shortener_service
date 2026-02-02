package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private final char[] alphabet;
    private final int base;
    private final int acceptableHashSize;

    public Base62Encoder(
            @Value("${base62.alphabet}") String alphabet,
            @Value("${base62.size}") int base,
            @Value("${hash.size}") int acceptableHashSize
    ) {
        this.alphabet = alphabet.toCharArray();
        this.base = base;
        this.acceptableHashSize = acceptableHashSize;
    }

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            result.add(encodeSingle(number));
        }

        return result;
    }

    private String encodeSingle(long number) {
        String encoded = encodeBase62(number);

        if (encoded.length() > acceptableHashSize) {
            throw new IllegalStateException(
                    "Encoded hash length exceeds limit: " + encoded
            );
        }

        return encoded;
    }

    private String encodeBase62(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }

        if (number == 0) {
            return String.valueOf(alphabet[0]);
        }

        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % base);
            sb.append(alphabet[remainder]);
            number /= base;
        }

        return sb.reverse().toString();
    }
}