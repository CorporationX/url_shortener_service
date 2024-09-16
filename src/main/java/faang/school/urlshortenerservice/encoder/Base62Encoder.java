package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE_62_CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        if (number == 0) {
            return String.valueOf(BASE_62_CHARACTERS.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encoded.append(BASE_62_CHARACTERS.charAt(remainder));
            number /= BASE;
        }

        return encoded.reverse().toString();
    }
}