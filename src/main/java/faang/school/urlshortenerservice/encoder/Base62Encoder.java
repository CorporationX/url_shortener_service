package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Base62Encoder {

    private final int base = 62;
    private final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        List<Hash> hashes = new ArrayList<>();
        for (Long number : numbers) {
            StringBuilder stringBuilder = new StringBuilder(1);
            do {
                stringBuilder.insert(0, characters.charAt((int) (number % base)));
                number /= base;
            } while (number > 0);
            hashes.add(new Hash(stringBuilder.toString()));
        }
        return hashes;
    }

}
