package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final char[] BASE62 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public String encode(long number) {
        StringBuilder string = new StringBuilder();
        do {
            int remainder = Math.round(number % 62);
            string.insert(0, BASE62[remainder]);
            number /= 62;
        } while (number > 0);
        return string.toString();
    }
}
