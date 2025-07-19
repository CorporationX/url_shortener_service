package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .collect(Collectors.toList());
    }

    private String encodeNumber(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARACTERS.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(BASE62_CHARACTERS.charAt(remainder));
            number = number / BASE;
        }
        return result.reverse().toString();
    }
}