package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${hash.encoder.base62.value}")
    private String base62;

    private int base;

    @PostConstruct
    private void init() {
        this.base = base62.length();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumberToBase62)
                .toList();
    }

    private String encodeNumberToBase62(long number) {
        if (number == 0) {
            return String.valueOf(base62.charAt(0));
        }
        StringBuilder hash = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base);
            hash.insert(0, base62.charAt(remainder));
            number /= base;
        }
        return hash.toString();
    }
}
