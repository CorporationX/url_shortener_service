package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
            .map(this::encode)
            .toList();
    }

    public String encode(Long number) {
        StringBuilder string = new StringBuilder();
        while (!number.equals(0L)) {
            int index = (int) (number % BASE);
            string.append(ALPHABET.charAt(index));
            number = number / BASE;
        }

        return string.toString();
    }
}
