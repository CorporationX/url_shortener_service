package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.util.api.Base62Encoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62EncoderImpl implements Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public String[] encodeNumbers(List<Long> numbers) {
        return numbers.stream()
            .map(this::encode)
            .toArray(String[]::new);
    }

    private String encode(Long number) {
        StringBuilder builder = new StringBuilder();
        while (!number.equals(0L)) {
            int index = (int) (number % BASE_62_CHARACTERS.length());
            builder.append(BASE_62_CHARACTERS.charAt(index));
            number = number / BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }
}