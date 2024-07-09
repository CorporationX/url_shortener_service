package encoder;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {

    private static final int BASE = 62;

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toBase62)
                .collect(Collectors.toList());
    }

    private String toBase62(Long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.insert(0, BASE62.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return result.toString();
    }
}
