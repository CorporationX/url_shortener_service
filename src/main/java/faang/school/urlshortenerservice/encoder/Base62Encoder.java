package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Value("${params.base-62-encoder.hash-max-length}")
    private int hashMaxLength;

    public List<Hash> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> new Hash(encodeNumber(number)))
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < hashMaxLength; i++){
            int remainder = (int) (number % BASE62_CHARS.length());
            encoded.append(BASE62_CHARS.charAt(remainder));
            number /= BASE62_CHARS.length();
        }
        return encoded.reverse().toString();
    }
}
