package faang.school.urlshortenerservice.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Base62Encoder {

    @Value("${encoder.base62.alphabet:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz}")
    private String base62Alphabet;

    @Value("${encoder.base62.base:62}")
    private int base;

    /**
     * Кодирует список чисел в Base62 хэши
     *
     * @param numbers список уникальных чисел
     * @return список Base62 хэшей
     */
    public List<String> encode(List<Long> numbers) {
        log.debug("Encoding {} numbers to Base62", numbers.size());

        List<String> hashes = numbers.stream()
                .map(this::encodeNumber)
                .collect(Collectors.toList());

        log.debug("Successfully encoded {} hashes", hashes.size());
        return hashes;
    }

    /**
     * Кодирует одно число в Base62
     *
     * @param number число для кодирования
     * @return Base62 хэш
     */
    private String encodeNumber(long number) {
        if (number == 0) {
            return String.valueOf(base62Alphabet.charAt(0));
        }

        StringBuilder hash = new StringBuilder();
        long num = number;

        while (num > 0) {
            int remainder = (int) (num % base);
            hash.insert(0, base62Alphabet.charAt(remainder));
            num = num / base;
        }

        return hash.toString();
    }
}