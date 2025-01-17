package faang.school.urlshortenerservice.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Value("${base62.base}")
    private int base;

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
            int remainder = (int) (current % base);
            sb.append(BASE62_ALPHABET.charAt(remainder));
            current /= base;
        }

        return sb.reverse().toString();
    }
}
