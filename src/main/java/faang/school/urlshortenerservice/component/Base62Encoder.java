package faang.school.urlshortenerservice.component;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@Validated
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;
    private static final int HASH_LENGTH = 6;

    public List<String> encode(@NonNull List<Long> numbers) {
        return numbers.stream()
                .map(this::convertToBase62)
                .toList();
    }

    private String convertToBase62(@NonNull Long number) {
        if (number == 0) {
            return "0".repeat(HASH_LENGTH);
        }

        StringBuilder hash = new StringBuilder();
        long num = number;
        while (num > 0 && hash.length() < HASH_LENGTH) {
            int remainder = (int) (num % BASE);
            hash.insert(0, BASE62_CHARS.charAt(remainder));
            num /= BASE;
        }

        while (hash.length() < HASH_LENGTH) {
            hash.insert(0, '0');
        }

        return hash.toString();
    }
}
