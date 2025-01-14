package faang.school.urlshortenerservice.service.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    @Value("${app.base62_chars}")
    private String base62Chars;

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    StringBuilder encoded = new StringBuilder();
                    while (number > 0) {
                        int remainder = (int) (number % 62);
                        encoded.append(base62Chars.charAt(remainder));
                        number /= 62;
                    }
                    return new Hash(encoded.toString());
                }).toList();
    }
}