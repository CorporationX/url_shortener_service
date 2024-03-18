package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    @Value("${hashgenerator.base62-alphabet}")
    private final String base62Alphabet;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(base62Alphabet.charAt((int) (number % base62Alphabet.length())));
            number /= base62Alphabet.length();
        }
        return sb.toString();
    }

}
