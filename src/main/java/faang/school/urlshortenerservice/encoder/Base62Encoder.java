package faang.school.urlshortenerservice.encoder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class Base62Encoder {

    public static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        log.info("encoding numbers: {}", numbers);

        if (numbers == null) {
            return List.of();
        }
        return numbers
                .stream()
                .map(this::encode)
                .toList();
    }

    public String encode(Long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int index = (int) (number % BASE_62_CHARACTERS.length());
            result.append(BASE_62_CHARACTERS.charAt(index));
            number /= 10;
        }
        return result.toString();
    }
}
