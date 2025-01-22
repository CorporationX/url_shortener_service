package faang.school.urlshortenerservice.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        checkLongList(numbers);
        List<String> encodedHashes = new ArrayList<>();
        for (Long number : numbers) {
            encodedHashes.add(encodeToBase62(number));
        }
        return encodedHashes;
    }

    private String encodeToBase62(Long number) {
        StringBuilder result = new StringBuilder();

        do {
            int remainder = (int) (number % BASE);
            result.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        } while (number > 0);

        String encoded = result.reverse().toString();
        return String.format("%6s", encoded).replace(' ', '0');
    }

    void checkLongList(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty.");
        }
        for (Long number : numbers) {
            if (number < 0) {
                throw new IllegalArgumentException("List must contain only Long values.");
            }
        }
    }
}
