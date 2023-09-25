package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase62)
                .toList();
    }

    // package private modifier for comfortable testing

    String encodeBase62(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (number % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            number /= 62;
        } while (number > 0);
        return sb.reverse().toString();
    }
}

