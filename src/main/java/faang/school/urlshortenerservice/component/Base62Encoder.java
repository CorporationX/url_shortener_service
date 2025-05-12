package faang.school.urlshortenerservice.component;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String base62Symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = base62Symbols.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        numbers.forEach(number -> hashes.add(createHashString(number)));
        return hashes;
    }

    private String createHashString(long number) {
        StringBuilder hashBuilder = new StringBuilder();

        while (number > 0) {
            int remainder = (int)(number % BASE);
            hashBuilder.append(base62Symbols.charAt(remainder));
            number /= BASE;
        }
        return hashBuilder.toString();
    }
}
