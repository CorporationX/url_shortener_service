package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(Base62Encoder::encodeBase62)
                .map(Hash::new)
                .toList();
    }

    private String encodeBase62(long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.append(BASE62_ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }

        return encoded.reverse().toString();
    }
}