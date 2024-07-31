package faang.school.urlshortenerservice.service.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class Base62Encoder {

    private static final int BASE62 = 62;

    private static final int LENGTH = 6;

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encodeAll(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeEach)
                .toList();
    }

    private Hash encodeEach(Long number) {
        StringBuilder stringBuilder = new StringBuilder(1);

        do {
            stringBuilder.insert(0, CHARACTERS.charAt((int) (number % BASE62)));
            number /= BASE62;
        } while (number > 0);

        Random random = new Random();

        while (stringBuilder.length() < LENGTH) {
            stringBuilder.insert(0, CHARACTERS.charAt(random.nextInt(BASE62)));
        }

        Hash hash = new Hash();
        hash.setHash(stringBuilder.toString());
        return hash;
    }
}
