package faang.school.urlshortenerservice.service.base62Encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> generateHashList(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeBase62)
                .toList();
    }

    public String encodeBase62(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE62_CHARACTERS.length());
            encoded.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE62_CHARACTERS.length();
        }
        return encoded.reverse().toString();
    }
}
