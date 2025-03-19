package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int BASE = BASE62_CHARS.length();

    public List<String> encode(List<Long> numbers) {
        List<String> encode = new ArrayList<>();

        numbers.forEach(number -> {
            String encodeNumber = encodeToStr(number);
            encode.add(encodeNumber);
        });
        return encode;
    }

    private String encodeToStr(Long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encoded.insert(0, BASE62_CHARS.charAt(remainder));
            number /= BASE;
        }
        return encoded.toString();
    }
}
