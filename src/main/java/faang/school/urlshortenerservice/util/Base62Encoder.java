package faang.school.urlshortenerservice.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Base62Encoder {
    private static final String BASE_62_SYMBOLS = "PTIWaXgjHdfDCrR1YZuVUF9Sm78Q46AJoweh3psvKOiyqnBkE2lMG0c5txLzbN";

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        numbers.forEach(number -> hashes.add(generateHash(number)));
        return hashes;
    }

    private String generateHash(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE_62_SYMBOLS.charAt((int) (number % BASE_62_SYMBOLS.length())));
            number /= BASE_62_SYMBOLS.length();
        }
        return sb.toString();
    }
}
