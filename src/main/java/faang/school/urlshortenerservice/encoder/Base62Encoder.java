package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    public static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(number -> {
                    if (number <= 0) {
                        throw new DataValidationException("Число для кодировки должно быть больше 0");
                    }
                    StringBuilder encoded = new StringBuilder();
                    while (number > 0) {
                        int remainder = (int) (number % 62);
                        encoded.append(BASE62_ALPHABET.charAt(remainder));
                        number /= 62;
                    }
                    return encoded.reverse().toString();
                })
                .toList();
    }

}
