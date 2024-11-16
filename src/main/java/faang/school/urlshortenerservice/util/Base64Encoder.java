package faang.school.urlshortenerservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base64Encoder {
    private static final String BASE_62_CHARACTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int maxLength;

    public Base64Encoder(@Value("${hash.max-length:6}") int maxLength) {
        this.maxLength = maxLength;
    }

    public String applyBase62Encoding(long number) {
        StringBuilder stringBuilder = new StringBuilder(maxLength);
        while (number > 0) {
            stringBuilder.append(BASE_62_CHARACTER.charAt((int) (number % BASE_62_CHARACTER.length())));
            number /= BASE_62_CHARACTER.length();
        }
        return stringBuilder.toString();
    }
}
