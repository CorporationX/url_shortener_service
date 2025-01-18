package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARACTERS.length();

    public List<Hash> encode(List<Long> numbers) {
        List<Hash> encodedHashes = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            encodedHashes.add(new Hash(encodeBase62(number)));
        }
        return encodedHashes;
    }

    private String encodeBase62(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE;
        }
        return encoded.reverse().toString();
    }
}
