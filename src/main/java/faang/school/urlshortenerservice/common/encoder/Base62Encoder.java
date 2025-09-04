package faang.school.urlshortenerservice.common.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder implements Encoder {
    private static final String BASE_62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

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
                int remainder = (int) (number % BASE_62.length());
                base62.append(BASE_62.charAt(remainder));
                number /= BASE_62.length();
            }
            hashes.add(base62.toString());
        });

        return hashes;
    }

}
