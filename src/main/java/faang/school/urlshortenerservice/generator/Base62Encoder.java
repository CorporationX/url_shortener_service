package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CHARS_LENGTH = BASE62_CHARS.length();
    private static final int HASH_LENGTH = 6;

    public Set<String> encode(List<Long> numbers) {
        StringBuilder sb = new StringBuilder();
        return numbers.stream().map(num -> {
            while (num > 0) {
                long remainder = num % CHARS_LENGTH;
                sb.append(BASE62_CHARS.charAt((int) remainder));
                num /= CHARS_LENGTH;
            }
            sb.reverse();
            if (sb.length() > HASH_LENGTH) {
                sb.setLength(HASH_LENGTH);
            }
            return sb.toString();
        }).collect(Collectors.toSet());
    }
}
