package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String HASH = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int HASH_LENGTH = 62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int digit = (int) (number % HASH_LENGTH);
            result.append(HASH.charAt(digit));
            number /= HASH_LENGTH;
        }
        return result.toString();
    }
}
