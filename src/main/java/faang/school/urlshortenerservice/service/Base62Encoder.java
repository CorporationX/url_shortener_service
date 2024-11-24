package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final String BASE62_CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int BASE_62_LENGTH = BASE62_CHAR_SET.length();

    public List<String> encode(List<Long> uniqueValues) {
        return uniqueValues.stream()
                .map(this::encodeBase62)
                .toList();
    }

    public String encodeBase62(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE_62_LENGTH);
            sb.append(BASE62_CHAR_SET.charAt(remainder));
            number /= BASE_62_LENGTH;
        }
        return sb.reverse().toString();
    }
}
