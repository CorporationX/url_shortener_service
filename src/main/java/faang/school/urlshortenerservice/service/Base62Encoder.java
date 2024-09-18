package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private final static String STRING_62
            = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {

        return numbers.stream()
                .map(this::encodeToBase62)
                .map(Hash::new)
                .toList();
    }

    private String encodeToBase62(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int base = STRING_62.length();
            sb.insert(0, STRING_62.charAt((int) (number % base)));
            number /= base;
        }
        return sb.toString();
    }
}
