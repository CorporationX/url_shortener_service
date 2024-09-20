package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private final static int BASE = 62;
    private final static char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();


    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        if (number == 0) {
            return "0";
        }
        boolean negative = false;
        if (number < 0) {
            negative = true;
            number = -number;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (number != 0) {
            stringBuilder.append(ALPHABET[(int) number % BASE]);
            number = number / BASE;
        }
        if (negative) {
            stringBuilder.append("-");
        }
        return stringBuilder.reverse().toString();
    }
}
