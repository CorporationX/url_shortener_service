package faang.school.urlshortenerservice.encoder;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public List<String> encode(List<Long> number) {
        return number.stream()
                .map(this::encodeNumbers)
                .collect(Collectors.toList());
    }
    private String encodeNumbers(Long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder hash = new StringBuilder();
        long num = Math.abs(number);
        while (num > 0) {
            int remainder = (int) (num % BASE);
            hash.append(BASE62_CHARS.charAt(remainder));
            num /= BASE;
        }
        return hash.reverse().toString();
    }

}
