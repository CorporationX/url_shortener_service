package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {
    @Value("${base62.chars}")
    private String base62Chars;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeForOneNum)
                .toList();
    }

    private String encodeForOneNum(Long num) {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            stringBuilder.append(base62Chars.charAt((int) (num % base62Chars.length())));
            num /= base62Chars.length();
        } while (num > 0);
        return stringBuilder.reverse().toString();
    }
}
