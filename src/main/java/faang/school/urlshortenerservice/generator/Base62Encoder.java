package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int MAX_HASH_SIZE = 6;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toBase62)
                .toList();
    }

    private String toBase62(Long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE62.charAt((int) (number % BASE62.length())));
            number /= BASE62.length();
        }

        return builder.length() > MAX_HASH_SIZE ? builder.substring(0, MAX_HASH_SIZE) : builder.toString();
    }
}
