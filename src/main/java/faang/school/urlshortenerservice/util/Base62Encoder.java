package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${encoder.length}")
    private int base;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder result = new StringBuilder(1);

        do {
            result.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);

        return result.toString();
    }
}
