package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_LENGTH = 62;

    public List<Hash> encodeList(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .map(Hash::new)
                .toList();
    }

    public String encode(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.append(BASE62.charAt((int) (number % BASE_LENGTH)));
            number /= BASE_LENGTH;
        }
        return encoded.toString();
    }
}
