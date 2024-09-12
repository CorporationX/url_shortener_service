package faang.school.urlshortenerservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseEncoder {
    @Value("${base-encoder.base}")
    private int base;
    @Value("${base-encoder.characters}")
    private String characters;

    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }
    public long decode(String number) {
        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(base, i) * characters.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }
}
