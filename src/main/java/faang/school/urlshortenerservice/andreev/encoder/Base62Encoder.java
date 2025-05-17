package faang.school.urlshortenerservice.andreev.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(this::toBase62).toList();
    }

    private String toBase62(long number) {
        if (number == 0) {
            return String.valueOf(BASE_62_CHARACTERS.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }

        while (result.length() < 6) {
            result.append(BASE_62_CHARACTERS.charAt(0));
        }

        return result.reverse().toString();
    }
}
