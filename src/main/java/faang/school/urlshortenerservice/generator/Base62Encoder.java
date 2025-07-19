package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE_62_CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeOne)
                .toList();
    }

    public String encodeOne(long value) {
        if (value == 0) {
            return String.valueOf(BASE_62_CHARACTERS.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE_62_CHARACTERS.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.toString();
    }
}
