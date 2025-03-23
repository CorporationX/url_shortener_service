package faang.school.urlshortenerservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Base62Encoder {
    private final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE = BASE62.length();

    public String encode(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            builder.append(BASE62.charAt(remainder));
            number /= BASE;
        }
        return builder.toString();
    }
}
