package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encode(number));
        }
        return hashes;
    }

    public String encode(long value) {
        if (value == 0) {
            return "0";
        }

        StringBuilder encoded = new StringBuilder();

        while (value > 0) {
            encoded.append(BASE62.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return encoded.reverse().toString();
    }
}
