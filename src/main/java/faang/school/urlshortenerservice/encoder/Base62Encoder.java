package faang.school.urlshortenerservice.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Value("${length.start:5}")
    private int length;

    public Set<String> encode(Set<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumberToBase62)
                .collect(Collectors.toSet());
    }

    private String encodeNumberToBase62(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            number = encodeNextDigit(number, result);
        }
        return result.toString();
    }

    private long encodeNextDigit(long number, StringBuilder result) {
        int index = (int) (number % length);
        result.append(ALPHABET.charAt(index));
        return number / length;
    }
}
