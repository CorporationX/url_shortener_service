package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private final int ENCODING_FACTOR = 62;

    @Value("{hashgenerator.base62-alphabet}")
    private final String base62Alphabet;

    public List<String> encode(List<Long> numbers) {
        List<String> encodedStrings = new ArrayList<>();
        for (Long number : numbers) {
            encodedStrings.add(encodeBase62(number));
        }
        return encodedStrings;
    }

    private String encodeBase62(Long value) {
        if (value == 0) {
            return String.valueOf(base62Alphabet.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(base62Alphabet.charAt((int)(value % ENCODING_FACTOR)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}
