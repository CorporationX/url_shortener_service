package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    @Value("${app.base62_chars}")
    private String base62Chars;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    StringBuilder encoded = new StringBuilder();
                    while (number > 0) {
                        int remainder = (int) (number % 62);
                        encoded.append(base62Chars.charAt(remainder));
                        number /= 62;
                    }
                    return encoded.toString();
                }).toList();
    }
}