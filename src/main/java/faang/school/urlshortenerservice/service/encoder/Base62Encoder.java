package faang.school.urlshortenerservice.service.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Base62Encoder {
    private static final String BASE62_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62_CHARSET.length();

    public List<String> generateHash(List<Long> uniqueNumbers) {
        return uniqueNumbers.stream()
                .map(this::encode)
                .toList();
    }

    public String encode(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARSET.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.append(BASE62_CHARSET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return encoded.reverse().toString();
    }

}
