package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.hash.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE_62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .map(Hash::new)
                .toList();
    }

    private String encodeNumber(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE_62.charAt((int) (number % BASE_62.length())));
            number /= BASE_62.length();
        }
        return sb.toString();
    }
}
