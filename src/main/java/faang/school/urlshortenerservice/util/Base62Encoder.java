package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String encodeNumber(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE62_ALPHABET.charAt((int) (number % BASE62_ALPHABET.length())));
            number /= BASE62_ALPHABET.length();
        }
        return builder.toString();
    }

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            hashes.add(encodeNumber(number));
        }
        return hashes;
    }
}