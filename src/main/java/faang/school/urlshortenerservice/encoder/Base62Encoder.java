package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Base62Encoder implements Encoder{
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(Long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE62_CHARS.charAt((int)(number % 62)));
            number /= 62;
        }
        return sb.reverse().toString();
    }
}