package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62.length();

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            result.add(encodeSingle(number));
        }

        return result;
    }

    private String encodeSingle(Long number) {
        if (number == 0L) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        long current = number;

        while (current > 0) {
            int remainder = (int) (current % BASE);
            sb.append(BASE62.charAt(remainder));
            current /= BASE;
        }

        return sb.reverse().toString();
    }
}
