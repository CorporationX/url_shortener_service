package encoder;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toBase62)
                .collect(Collectors.toList());
    }

    private String toBase62(Long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.insert(0, BASE62.charAt((int) (number % 62)));
            number /= 62;
        }
        return result.toString();
    }
}
