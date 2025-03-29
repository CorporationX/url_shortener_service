package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            int index = (int) (num % BASE);
            sb.append(BASE62_ALPHABET.charAt(index));
            num /= BASE;
        }

        return sb.reverse().toString();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(Base62Encoder::encode)
                .collect(Collectors.toList());
    }
}