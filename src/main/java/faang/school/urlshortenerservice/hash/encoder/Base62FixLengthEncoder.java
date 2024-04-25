package faang.school.urlshortenerservice.hash.encoder;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Base62FixLengthEncoder implements HashEncoder {

    @Value("${encoder.base62Chars}")
    private String base62Chars;
    private int codeLength;
    @Value("${encoder.hash-length}")
    private int hashLength;

    @PostConstruct
    private void init() {
        codeLength = base62Chars.length();
    }

    @Override
    public Set<String> encode(List<Long> uniqueNumbers) {
        return uniqueNumbers.stream()
                .map(this::encodeToFixLength)
                .collect(Collectors.toSet());
    }

    private String encodeToFixLength(Long uniqueNumber) {
        StringBuilder encoded = new StringBuilder();
        while (uniqueNumber > 0) {
            int index = (int) (uniqueNumber % codeLength);
            encoded.append(base62Chars.charAt(index));
            uniqueNumber /= codeLength;
        }
        while (encoded.length() < hashLength) {
            encoded.append(base62Chars.charAt(0));
        }
        return encoded.toString();
    }

}
