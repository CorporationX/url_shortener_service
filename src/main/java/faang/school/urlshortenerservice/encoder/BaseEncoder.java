package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaseEncoder {
    @Value("${hash.base}")
    private int base;
    @Value("${hash.alphabet}")
    private String alphabet;

    public String encodeNumber(long n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append(alphabet.charAt((int) (n % base)));
            n /= base;
        }
        sb.reverse();
        return sb.toString();
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::encodeNumber).toList();
    }
}