package faang.school.urlshortenerservice.encoder;


import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_KEYS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            long remainder = number % BASE62_KEYS.length();
            stringBuilder.append(BASE62_KEYS.charAt((int) remainder));
            number = number / BASE62_KEYS.length();
        }
        return stringBuilder.toString();
    }
}
