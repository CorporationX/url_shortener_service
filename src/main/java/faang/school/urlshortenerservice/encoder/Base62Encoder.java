package faang.school.urlshortenerservice.encoder;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE62_LENGTH = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> number) {
        return number.stream()
                .map(this::encodeNumbers)
                .toList();
    }

    private String encodeNumbers(Long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder hash = new StringBuilder();
        long num = Math.abs(number);
        while (num > 0) {
            int remainder = (int) (num % BASE62_LENGTH);
            hash.append(BASE62_ALPHABET.charAt(remainder));
            num /= BASE62_LENGTH;
        }
        return hash.reverse().toString();
    }

}
