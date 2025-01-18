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
        List<String> encodedHashes = new ArrayList<>();

        for (Long number : numbers) {
            encodedHashes.add(encodeToBase62(number));
        }

        return encodedHashes;
    }

    private String encodeToBase62(Long number) {
        if (number == null || number < 0) {
            log.error("Number must be a non-negative value.{}", number);
            throw new IllegalArgumentException("Number must be a non-negative value.");
        }

        StringBuilder result = new StringBuilder();

        do {
            int remainder = (int) (number % BASE);
            result.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        } while (number > 0);

        return result.reverse().toString();
    }
}
