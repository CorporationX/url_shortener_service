package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    private String encodeNumber(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        int alphabetSize = BASE62_CHARS.length();

        do {
            stringBuilder.insert(0, BASE62_CHARS.charAt((int) (number % alphabetSize)));
            number /= alphabetSize;
        } while (number > 0);

        return stringBuilder.toString();
    }
}
