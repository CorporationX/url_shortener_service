package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> uniqueNumbers) {
        return uniqueNumbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(ALPHABET.charAt((int) (number % ALPHABET.length())));
            number /= ALPHABET.length();
        }
        return builder.reverse().toString();
    }
}
