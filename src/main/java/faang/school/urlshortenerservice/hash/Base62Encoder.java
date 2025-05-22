package faang.school.urlshortenerservice.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    private final Integer maxHashLength;

    public Base62Encoder(@Value("${hash.length}") Integer maxHashLength) {
        this.maxHashLength = maxHashLength;
    }

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            hashes.add(encodeNumberToBase62(number));
        }

        return hashes;
    }

    private String encodeNumberToBase62(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }

        String encoded = result.reverse().toString();

        return encoded.length() > maxHashLength
                ? encoded.substring(0, maxHashLength)
                : encoded;
    }
}
