package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();
    @Value( "${hash.hash-generator.max-hash-length}")
    private int MAX_HASH_LENGTH;

    public List<String> encode(List<Long> numbers) {
        numbers.removeIf(number -> number < 0);
        return numbers.stream()
                .map(number -> {
                    StringBuilder encoded = new StringBuilder();
                    while (number > 0) {
                        int remainder = (int) (number % BASE);
                        encoded.append(BASE62_CHARS.charAt(remainder));
                        number /= BASE;
                    }
                    String result = encoded.reverse().toString();

                    if (result.length() > MAX_HASH_LENGTH) {
                        result = result.substring(0, MAX_HASH_LENGTH);
                    }

                    return result;
                })
                .toList();
    }
}
