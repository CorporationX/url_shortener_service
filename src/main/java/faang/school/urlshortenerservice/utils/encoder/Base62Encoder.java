package faang.school.urlshortenerservice.utils.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${hash.length:6}")
    private int hashLength;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(num -> {
                    StringBuilder hash = new StringBuilder();
                    while (num > 0) {
                        hash.append(BASE62_ALPHABET.charAt((int) (num % BASE62_ALPHABET.length())));
                        num /= BASE62_ALPHABET.length();
                    }
                    String result = hash.reverse().toString();
                    // Проверка на длину хэша
                    if (result.length() > hashLength) {
                        throw new IllegalStateException("Хэш превышает максимальную длину: " + hashLength + " символов: " + result);
                    }
                    return result;
                })
                .toList();
    }
}
