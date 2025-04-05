package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .toList();
    }

    private String encodeToBase62(Long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE62.charAt((int) (number % BASE62.length())));
            number /= BASE62.length();
        }
        return builder.toString();
    }
}
