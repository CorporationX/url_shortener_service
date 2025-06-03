package faang.school.urlshortenerservice.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    @Value("${hash.alphabet:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz}")
    private String alphabet;

    @PostConstruct
    public void init() {
        log.info("Initializing Base62Encoder with alphabet: {}", alphabet);
        if (alphabet == null || alphabet.isBlank()) {
            throw new IllegalArgumentException("Alphabet cannot be null or blank");
        }
        if (alphabet.length() != 62) {
            throw new IllegalArgumentException("Alphabet must contain at most 62 characters");
        }
        if (alphabet.chars().distinct().count() != alphabet.length()) {
            throw new IllegalArgumentException("Alphabet must contain unique characters");
        }
        log.info("Base62Encoder initialization complete.");
    }

    private String encodeNumber(long number) {
        StringBuilder encoded = new StringBuilder();
        if (number == 0) {
            return String.valueOf(alphabet.charAt(0));
        }
        while (number > 0) {
            encoded.insert(0, alphabet.charAt((int) (number % alphabet.length())));
            number /= alphabet.length();
        }
        return encoded.toString();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }
}
