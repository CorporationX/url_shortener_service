package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        List<String> encodedList = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            encodedList.add(encodeNumber(number));
        }
        return encodedList;
    }

    private String encodeNumber(Long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE62_CHARACTERS.length());
            sb.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE62_CHARACTERS.length();
        }
        return sb.reverse().toString();
    }
}
