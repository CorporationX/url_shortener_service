package faang.school.urlshortenerservice.hash;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

    public List<String> encode(List<Long> numbers) {
        return numbers
                .stream()
                .map(this::encodeSingleNumber)
                .toList();
    }

    private String encodeSingleNumber(Long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            int reminder = (int) (number % BASE);
            builder.append(BASE62_CHARS.charAt(reminder));
            number = number / BASE;
        }
        return builder.reverse().toString();
    }
}
