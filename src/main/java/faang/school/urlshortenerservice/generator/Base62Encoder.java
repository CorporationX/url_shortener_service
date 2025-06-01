package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeSingle)
                .toList();
    }

    private String encodeSingle(Long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(ALPHABET.charAt((int)(num % BASE)));
            num /= BASE;
        }
        return sb.reverse().toString();
    }
}