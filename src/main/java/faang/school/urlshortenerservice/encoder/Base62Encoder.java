package faang.school.urlshortenerservice.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    public static final String BASE_62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(BASE_62_CHARS.charAt((int) (number % BASE_62_CHARS.length())));
            number /= BASE_62_CHARS.length();
        }
        return stringBuilder.toString();
    }
}
