package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final static String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final static int BASE = 62;

    public List<String> encodeList(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    public String encode(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return encoded.reverse().toString();
    }
}
