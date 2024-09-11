package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public List<String> encode(List<Long> numbers){
        return numbers.stream()
                .map(this::base62Encode)
                .toList();
    }

    private String base62Encode(Long number){
        StringBuilder result = new StringBuilder();

        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(BASE62_CHARS.charAt(remainder));
            number /= BASE;
        }

        return result.reverse().toString();
    }
}
