package faang.school.urlshortenerservice.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public List<String> applyBase62Encoding(List<Long> numbers) {
        StringBuilder builder = new StringBuilder();
        List<String> encodedNumbers = new ArrayList<>();
        for (Long number : numbers) {
            while (number > 0) {
                builder.append(BASE62_CHARACTERS.charAt((int) (number % BASE62_CHARACTERS.length())));
                number /= BASE62_CHARACTERS.length();
            }
            encodedNumbers.add(builder.toString());
        }
        return encodedNumbers;
    }

}
