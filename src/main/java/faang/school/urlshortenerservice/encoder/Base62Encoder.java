package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        StringBuilder sb = new StringBuilder();

        for (Long number : numbers) {
            if (number == 0) {
                hashes.add(String.valueOf(BASE62_CHARS.charAt(0)));
                continue;
            }

            sb.setLength(0);
            while (number > 0) {
                int remainder = (int) (number % BASE);
                sb.insert(0, BASE62_CHARS.charAt(remainder));
                number /= BASE;
            }
            hashes.add(sb.toString());
        }
        return hashes;
    }
}
