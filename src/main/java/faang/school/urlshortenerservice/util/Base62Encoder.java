package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${hash.encoder.template}")
    private String encodeTemplate;

    @Value("${hash.encoder.base-length}")
    private int baseLength;

    public String[] generateHashes(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumberToHash)
                .toArray(String[]::new);
    }

    private String encodeNumberToHash(long num) {
        StringBuilder encoded = new StringBuilder();

        while (num > 0) {
            int remainder = (int) (num % baseLength);
            encoded.append(encodeTemplate.charAt(remainder));
            num /= baseLength;
        }
        return encoded.toString();
    }
}

