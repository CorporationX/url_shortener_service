package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Value("${hash.max-length}")
    private int maxHashLength;

    public String encode(long number) {
        if (number <= 0) {
            throw new DataValidationException("Число для кодировки должно быть больше 0");
        }
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 62);
            encoded.append(BASE62_ALPHABET.charAt(remainder));
            number /= 62;
        }
        if (encoded.length() < maxHashLength) {
            return encoded.reverse().toString();
        } else {
            throw new IllegalStateException("Нельзя сгенерировать хэш больше %s символов"
                    .formatted(maxHashLength));
        }
    }
}


