package faang.school.urlshortenerservice.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Base62Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> hashes = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            hashes.add(encodeNumber(number));
        }

        log.debug("Encoded {} numbers into unique hashes", numbers.size());
        return hashes;
    }

    private String encodeNumber(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        long remaining = number;

        while (remaining > 0) {
            int remainder = (int) (remaining % BASE);
            sb.append(ALPHABET.charAt(remainder));
            remaining /= BASE;
        }

        return sb.reverse().toString();
    }
}
