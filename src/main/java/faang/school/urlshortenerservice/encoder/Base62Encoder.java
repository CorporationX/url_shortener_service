package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final static String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${hash.encoder.size}")
    private int hashSize;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long number) {
        StringBuilder result = new StringBuilder();
        int alphabetSize = alphabet.length();
        for (int i = 0; i < hashSize; i++) {
            result.append(alphabet.charAt((int) (number % alphabetSize)));
            number /= alphabetSize;
        }
        return result.toString();
    }
}
