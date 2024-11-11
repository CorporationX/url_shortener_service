package faang.school.urlshortenerservice.util.encode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    @Value("${app.encoder.base_62.characters}")
    private String characters;

    @Value("${app.encoder.base_62.base}")
    private int base;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .toList();
    }

    private String encodeToBase62(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base);
            encoded.append(characters.charAt(remainder));
            number /= base;
        }
        return encoded.reverse().toString();
    }
}
