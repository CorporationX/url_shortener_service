package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long number) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(ALPHABET.charAt((int) (number % 62)));
            number /= 62;
        }
        return result.toString();
    }
}
