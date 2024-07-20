package faang.school.urlshortenerservice.service.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final int BASE62 = 62;

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toBase62)
                .collect(Collectors.toList());
    }

    private Hash toBase62(Long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, CHARACTERS.charAt((int) (number % BASE62)));
            number /= BASE62;
        } while (number > 0);

        Hash hash = new Hash();
        hash.setHash(stringBuilder.toString());
        return hash;
    }
}
