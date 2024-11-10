package faang.school.urlshortenerservice.model.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${base62.alphabet}")
    private String BASE62;
    @Value("${base62.length}")
    private int base62Length;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    public String encodeNumber(long num) {
        StringBuilder encoded = new StringBuilder();

        while (num > 0) {
            int remainder = (int) (num % base62Length);
            encoded.append(BASE62.charAt(remainder));
            num /= base62Length;
        }
        return encoded.toString();
    }
}
