package faang.school.urlshortenerservice.service.generator;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private final int BASE = 62;
    private final static String BASE_62_CHARACTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String encode(Long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int i = (int) (number % BASE);
            number /= BASE;
            result.append(BASE_62_CHARACTER.charAt(i));
        }
        return result.toString();
    }
}
