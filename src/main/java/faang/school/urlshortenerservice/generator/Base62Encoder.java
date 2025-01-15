package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            StringBuilder encoded = new StringBuilder();
            while (number != 0) {
                int remainder = (int) (number % BASE);
                encoded.append(BASE62_ALPHABET.charAt(remainder));
                number /= BASE;
            }
            hashes.add(encoded.reverse().toString());
        }
        return hashes;
    }
}