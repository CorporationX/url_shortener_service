package faang.school.urlshortenerservice.Service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final char[] BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = 62;

    List<String> encode(List<Long> numbers) {
        List<String> encoded = new ArrayList<String>();
        for (Long number : numbers) {
            encoded.add(encodeElement(number));
        }
        return encoded;
    }

    private String encodeElement(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET[0]);
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int digit = (int) (number % BASE);
            sb.append(BASE62_ALPHABET[digit]);
            number /= BASE;
        }
        return sb.reverse().toString();
    }
}
