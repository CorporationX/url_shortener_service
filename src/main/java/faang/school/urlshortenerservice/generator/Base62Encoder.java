package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeSingle)
                .map(Hash::new)
                .toList();
    }

    private String encodeSingle(long number) {
        if (number == 0) {
            return "";
        }
        int remainder = (int) (number % BASE62.length());
        return encodeSingle(number / BASE62.length()) + BASE62.charAt(remainder);
    }
}
