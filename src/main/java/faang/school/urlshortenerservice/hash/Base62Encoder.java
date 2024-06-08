package faang.school.urlshortenerservice.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder implements Encoder {
    @Value("${base62Encoder.characters}")
    private String BASE62;
    @Value("${base62Encoder.base}")
    private int base;
    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(num -> encode(num)).toList();
    }
    @Override
    public String encode(long number) {
        if (number == 0) {
            return Character.toString(BASE62.charAt(0));
        } else {
            StringBuilder sb = new StringBuilder();
            while (number > 0) {
                int remainder = (int) number % base;
                sb.append(BASE62.charAt(remainder));
                number /= base;
            }
            if (sb.toString().length() < 6) {
                while (sb.toString().length() < 6) {
                    sb.append(0);
                }
                sb.reverse();
            }
            return sb.toString();
        }
    }
}
