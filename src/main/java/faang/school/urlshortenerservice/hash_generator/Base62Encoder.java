package faang.school.urlshortenerservice.hash_generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    public static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int BASE = BASE62_CHARS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.parallelStream().map(Base62Encoder::encode).toList();
    }

    private static String encode(Long number) {
        if (number == 0) {
            return "0";
        }

        var result = new StringBuilder();
        while (number > 0) {
            result.append(BASE62_CHARS.charAt((int)(number % BASE)));
            number /= BASE;
        }

        return result.toString();
    }
}
