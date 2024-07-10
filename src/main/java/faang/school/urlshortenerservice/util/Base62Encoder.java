package faang.school.urlshortenerservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    public static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {

        if (number < 0) {
            throw new IllegalArgumentException("Number must be greater than or equal to 0");
        }

        StringBuilder stringBuilder = new StringBuilder(1);

        do {
            stringBuilder.insert(0, BASE62.charAt((int) (number % BASE62.length())));
            number /= BASE62.length();
        } while (number > 0);
        return stringBuilder.toString();
    }
}
