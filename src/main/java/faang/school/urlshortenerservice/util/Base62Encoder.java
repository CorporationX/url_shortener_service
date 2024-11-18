package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encodeNumbersInBase62(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumberInBase62)
                .toList();
    }

    public String encodeNumberInBase62(long number) {
        StringBuilder encoded = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE62_CHARS.length());
            encoded.append(BASE62_CHARS.charAt(remainder));
            number /= BASE62_CHARS.length();
        }
        return encoded.reverse().toString();
    }
}
