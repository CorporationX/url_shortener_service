package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(Set<Long> numbers) {
        List<String> encodedList = new ArrayList<>();
        for (Long number : numbers) {
            String encoded = encode(number);
            encodedList.add(encoded);
        }
        return encodedList;
    }

    private String encode(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_CHARS.charAt(remainder));
            number /= 62;
        } while (number > 0);
        return sb.toString();
    }
}
