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
            sb.append(BASE62.charAt((int) (number % BASE62.length())));
            number /= BASE62.length();
        }
        String encodedString = sb.toString();
        return encodedString.length() > 6 ? encodedString.substring(0, 6) : encodedString;
    }
}
