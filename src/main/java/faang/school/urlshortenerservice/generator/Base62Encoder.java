package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        StringBuilder builder = new StringBuilder();

        for (Long number : numbers) {
            builder.setLength(0);
            while (number > 0) {
                builder.append(BASE62[(int) (number % BASE62.length)]);
                number /= BASE62.length;
            }
            hashes.add(builder.toString());
        }
        return hashes;
    }
}
