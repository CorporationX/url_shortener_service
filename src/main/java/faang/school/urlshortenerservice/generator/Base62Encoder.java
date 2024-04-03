package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */
@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();

        for (Long originalNumber : numbers) {
            long number = originalNumber;
            StringBuilder sb = new StringBuilder();
            if (number == 0) {
                sb.append(BASE62_ALPHABET.charAt(0));
            } else {
                while (number > 0) {
                    sb.append(BASE62_ALPHABET.charAt((int) (number % BASE)));
                    number /= BASE;
                }
            }
            hashes.add(sb.toString());
        }
        return hashes;
    }
}
