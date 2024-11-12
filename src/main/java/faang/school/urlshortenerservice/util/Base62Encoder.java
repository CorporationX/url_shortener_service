package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encodeListNumbers(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase62)
                .toList();
    }

    public String encodeBase62(long value) {
        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            encoded.append(BASE62_CHARS.charAt((int) (value % BASE62_CHARS.length())));
            value /= BASE62_CHARS.length();
        }
        return encoded.reverse().toString();
    }
}
