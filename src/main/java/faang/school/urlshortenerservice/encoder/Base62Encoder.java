package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${params.base62.chars}")
    private String BASE62_CHARS;

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> new Hash(encodeNumber(number)))
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE62_CHARS.length());
            encoded.append(BASE62_CHARS.charAt(remainder));
            number /= BASE62_CHARS.length();
        }
        return encoded.reverse().toString();
    }
}
