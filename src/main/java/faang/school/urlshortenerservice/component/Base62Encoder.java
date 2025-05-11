package faang.school.urlshortenerservice.component;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .collect(Collectors.toList());
    }

    private String encodeNumber(long number) {
        if (number == 0) return "0";

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            number /= 62;
        }
        return sb.reverse().toString();
    }
}