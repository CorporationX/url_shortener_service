package faang.school.urlshortenerservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_LENGTH = BASE62_ALPHABET.length();

    @Value("${short-url.target-length}")
    private int targetLength;

    public List<String> encodeBatch(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int currentIndex = (int) (number % BASE_LENGTH);
            sb.append(BASE62_ALPHABET.charAt(currentIndex));
            number /= BASE_LENGTH;
        }

        while (sb.length() < targetLength) {
            sb.append('0');
        }

        return sb.reverse().toString();
    }
}
