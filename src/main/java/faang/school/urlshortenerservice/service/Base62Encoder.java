package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Component
public class Base62Encoder {
    private static final String BASE62_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::encode).toList();
    }

    private String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, BASE62_CHARS.charAt((int) (number % BASE62_CHARS.length())));
            number /= BASE62_CHARS.length();
        } while (number > 0);
        return stringBuilder.toString();
    }
}
