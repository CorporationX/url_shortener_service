package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = BASE62.length;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encodeNumberToBase62(number));
        }
        return hashes;
    }

    private String encodeNumberToBase62(long number) {
        StringBuilder hash = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            hash.insert(0, BASE62[remainder]);
            number /= BASE;
        }
        return hash.toString();
    }
}
