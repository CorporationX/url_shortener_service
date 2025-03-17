package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    public String encodeSingle(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            builder.append(BASE62.charAt(remainder));
            number /= BASE;
        }
        return builder.toString();
    }

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encodeSingle(number));
        }
        return hashes;
    }

}
