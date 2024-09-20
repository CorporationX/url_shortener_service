package faang.school.urlshortenerservice.encoder;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Setter
public class Base62Encoder {

    @Value("${hash.base62_charset}")
    private String base62Charsets;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .collect(Collectors.toList());
    }

    private String encodeNumber(long number) {
        StringBuilder sb = new StringBuilder();
        if (number == 0) {
            sb.append(base62Charsets.charAt(0));
        }
        while (number > 0) {
            int remainder = (int) (number % base62Charsets.length());
            sb.append(base62Charsets.charAt(remainder));
            number /= base62Charsets.length();
        }
        return sb.toString();
    }
}