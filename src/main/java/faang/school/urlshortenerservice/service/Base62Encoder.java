package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {

    @Value("${hash.alphabet}")
    private String base62Alphabet;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .filter(encodedNumber -> !encodedNumber.isBlank())
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder base62 = new StringBuilder();
        while (number > 0) {
            base62.append(base62Alphabet.charAt((int) (number % base62Alphabet.length())));
            number /= base62Alphabet.length();
        }

        return base62.toString();
    }
}
