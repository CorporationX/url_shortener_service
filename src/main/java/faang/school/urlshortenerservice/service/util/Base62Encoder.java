package faang.school.urlshortenerservice.service.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final byte BASE62_DIVIDER = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::longToBase62)
                .toList();
    }

    private String longToBase62(Long number) {
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(BASE62_ALPHABET.charAt((int) (number % BASE62_DIVIDER)));
            number /= BASE62_DIVIDER;
        }
        return stringBuilder.reverse().toString();
    }
}
