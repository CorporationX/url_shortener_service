package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${hash.generator.base62}")
    private String base62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long value) {
        StringBuilder encoded = new StringBuilder();
        int length = base62.length();

        while (value > 0) {
            encoded.append(base62.charAt((int) (value % length)));
            value /= length;
        }
        return encoded.toString();
    }
}
