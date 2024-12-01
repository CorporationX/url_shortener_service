package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE62_CHARACTERS.charAt((int) (number % BASE62_CHARACTERS.length())));
            number /= BASE62_CHARACTERS.length();
        }
        return String.format("%5s", sb.reverse()).replace(' ', '0');
    }
}