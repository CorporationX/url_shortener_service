package faang.school.urlshortenerservice.utils;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Base62Encoder {
    private static final char[] BASE62_CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE62_LENGTH = BASE62_CHARS.length;

    public List<String> encodeNumbers(@NotNull List<Long> numbers) {
        log.info("Encoding {} numbers to Base62", numbers.size());
        return numbers.stream().map(this::encode).toList();
    }

    private String encode(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            long remainder = number % BASE62_LENGTH;
            number = number / BASE62_LENGTH;
            encoded.append(BASE62_CHARS[(int) remainder]);
        }
        return encoded.reverse().toString();
    }
}