package faang.school.urlshortenerservice.common.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62EncoderImpl implements Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        numbers.forEach(number -> {
            if (number < 0) {
                log.error("Number must be non-negative, number: {}, data: {}", number, numbers);
                throw new IllegalArgumentException("Number must be non-negative");
            }
            StringBuilder base62 = new StringBuilder();
            while (number > 0) {
                base62.append(BASE_62_CHARACTERS.charAt((int) (number % 62)));
                number /= BASE_62_CHARACTERS.length();
            }
            hashes.add(base62.toString());
        });
        return hashes;
    }
}