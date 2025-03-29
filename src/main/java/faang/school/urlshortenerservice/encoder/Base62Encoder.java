package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public List<String> encode(List<Long> uniqueIds) {
        return uniqueIds.stream()
                .map(this::encodeBase62)
                .toList();
    }

    private String encodeBase62(long num) {
        StringBuilder builder = new StringBuilder();
        while (num > 0) {
            builder.append(BASE62_CHARACTERS.charAt((int) (num % BASE62_CHARACTERS.length())));
            num /= BASE62_CHARACTERS.length();
        }
        return builder.toString();
    }
}