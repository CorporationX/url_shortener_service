package faang.school.urlshortenerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${base62.alphabet}")
    private String BASE62_ALPHABET;
    @Value("${base62.length}")
    private int base62Length;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    public String encodeNumber(Long number) {
        StringBuilder result = new StringBuilder();

        while(number > 0) {
            int remainder = (int) (number % base62Length);
            result.append(BASE62_ALPHABET.charAt(remainder));
            number /= base62Length;
        }

        while (result.length() < 6) {
            result.append("0");
        }

        return result.reverse().toString();
    }
}
