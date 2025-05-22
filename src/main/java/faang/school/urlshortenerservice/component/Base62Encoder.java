package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.exceptions.EmptyNumbersListException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            log.info("Empty numbers list");
            throw new EmptyNumbersListException("The list of numbers cannot be null or empty.");
        }
        return numbers.stream()
                .map(this::encodeNumber)
                .collect(Collectors.toList());
    }

    private String encodeNumber(long number) {
        if (number == 0) return "0";

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE62_CHARS.length());
            sb.append(BASE62_CHARS.charAt(remainder));
            number /= BASE62_CHARS.length();
        }
        return sb.reverse().toString();
    }
}