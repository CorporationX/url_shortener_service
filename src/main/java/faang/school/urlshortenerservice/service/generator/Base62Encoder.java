package faang.school.urlshortenerservice.service.generator;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .toList();
    }

    private String encodeToBase62(long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(BASE62_CHARS.charAt(remainder));
            number /= BASE;
        }
        return result.toString();
    }
}
