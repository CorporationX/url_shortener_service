package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encodeListOfNumbers(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE62.charAt((int) (number % 62)));
            number /= 62;
        }
        return sb.reverse().toString();
    }
}
