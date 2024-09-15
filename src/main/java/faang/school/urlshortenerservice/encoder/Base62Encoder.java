package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encoder(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        for (Long number : numbers) {
            StringBuilder hash = new StringBuilder();
            while (number > 0) {
                int remainder = (int) (number % BASE);
                hash.append(BASE62_ALPHABET.charAt(remainder));
                number /= BASE;
            }
            hashes.add(hash.reverse().toString());
        }
        return hashes;
    }
}
