package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final int BASE = 62;
    private final static String BASE_62_CHARACTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> range) {
        return range.stream().map(this::toBase62Encode).map(Hash::new).toList();
    }

    private String toBase62Encode(Long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int i = (int) (number % BASE);
            number /= BASE;
            result.append(BASE_62_CHARACTER.charAt(i));
        }
        return result.toString();
    }
}
