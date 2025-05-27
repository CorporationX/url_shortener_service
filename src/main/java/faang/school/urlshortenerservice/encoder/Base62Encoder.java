package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE_LENGTH = BASE_62_CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::toBase62).toList();
    }

    private String toBase62(long number) {
        if (number == 0) {
            return String.valueOf(BASE_62_CHARACTERS.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_LENGTH)));
            number /= BASE_LENGTH;
        }
        return result.reverse().toString();
    }
}
