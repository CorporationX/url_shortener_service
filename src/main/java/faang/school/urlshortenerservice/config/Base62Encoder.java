package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    private static final String HASH = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        if (numbers == null || numbers.contains(null)) {
            log.error("Input list is null or contains null values");
            throw new IllegalArgumentException("Numbers list cannot be null or contain null values");
        }

        List<String> result = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            result.add(encodeNumber(number));
        }
        return result;
    }

    private String encodeNumber(long number) {
        if (number < 0) {
            log.error("Number must be non-negative: {}", number);
            throw new IllegalArgumentException("Number must be non-negative: " + number);
        }

        if (number == 0) {
            log.debug("Encoding zero");
            return String.valueOf(HASH.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.insert(0, HASH.charAt(remainder));
            number /= BASE;
        }

        return result.toString();
    }
}