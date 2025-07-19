package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>(numbers.size());
        for (Long num : numbers) {
            result.add(encodeSingle(num));
        }
        return result;
    }

    private String encodeSingle(Long number) {
        if (number == 0) return "0";
        StringBuilder encoded = new StringBuilder();
        Long num = number;
        while (num > 0) {
            encoded.append(BASE62.charAt((int) (num % 62)));
            num /= 62;
        }
        return encoded.reverse().toString();
    }
}
