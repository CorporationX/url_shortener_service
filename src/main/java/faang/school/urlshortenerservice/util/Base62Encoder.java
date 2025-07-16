package faang.school.urlshortenerservice.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    @Value("${base.chars}")
    private final String baseChars;

    public List<String> encode(List<Long> numbers) {
        List<String> encodedStrings = new ArrayList<>();
        if (numbers == null || numbers.isEmpty()) {
            return encodedStrings;
        }

        for (Long number : numbers) {
            StringBuilder encodedString = new StringBuilder();
            while (number > 0) {
                int remainder = (int) (number % baseChars.length());
                encodedString.insert(0, baseChars.charAt(remainder));
                number /= baseChars.length();
            }
            encodedStrings.add(encodedString.toString());
        }
        return encodedStrings;
    }
}
