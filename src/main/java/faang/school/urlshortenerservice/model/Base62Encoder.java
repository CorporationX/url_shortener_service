package faang.school.urlshortenerservice.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>(numbers.size());
        numbers.forEach(number -> hashes.add(encodeNumber(number)));
        return hashes;
    }

    private String encodeNumber(Long number) {
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }

        StringBuilder hashBuilder = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            hashBuilder.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return hashBuilder.reverse().toString();
    }
}
