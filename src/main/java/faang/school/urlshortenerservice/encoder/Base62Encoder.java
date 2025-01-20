package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String[] encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toArray(String[]::new);
    }

    public String encode(Long number) {
        StringBuilder builder = new StringBuilder();
        while (!number.equals(0L)) {
            int index = (int) (number % BASE_62_CHARACTERS.length());
            builder.append(BASE_62_CHARACTERS.charAt(index));
            number = number / BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }
}
