package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE62_BASE = BASE62_CHARACTERS.length();

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> new Hash(applyBase62Encoding(number)))
                .toList();
    }

    private String applyBase62Encoding(Long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE62_CHARACTERS.charAt((int) (number % BASE62_BASE)));
            number /= BASE62_BASE;
        }
        return builder.toString();
    }
}