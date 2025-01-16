package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {

    private final static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int BASE = ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toEncode)
                .toList();
    }

    private String toEncode(Long number) {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            long index = number % BASE;
            number = number / BASE;
            stringBuilder.append(ALPHABET.charAt((int) index));
        } while(number > 0);
        return stringBuilder.toString();
    }
}
