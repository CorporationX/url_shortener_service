package faang.school.urlshortenerservice.util.encoder;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Component
@RequiredArgsConstructor
public class Base62Encoder implements Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${hash.encoder.hash-size}")
    private int hashSize;

    public List<String> encodeBatch(List<Long> sequence) {
        return sequence.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long number) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hashSize; i++) {
            result.append(ALPHABET.charAt((int) (number % ALPHABET.length())));
            number /= ALPHABET.length();
        }
        return result.toString();
    }
}
