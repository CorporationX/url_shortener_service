package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int Base = BASE_62_CHARACTERS.length();

    public String encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE_62_CHARACTERS.charAt((int) (value % Base)));
            value /= Base;
        }
        return sb.toString();
    }
}
