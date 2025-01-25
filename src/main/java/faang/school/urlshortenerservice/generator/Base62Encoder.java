package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE62_LENGTH = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE62_LENGTH);
            encoded.append(BASE62.charAt(remainder));
            number /= BASE62_LENGTH;
        }
        return encoded.reverse().toString();
    }
}
