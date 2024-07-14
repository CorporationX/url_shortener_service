package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

@Component
public class Base62 {
    static final char[] BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static String base62(int n) {
        final StringBuilder sb = new StringBuilder();

        while (n > 0) {
            sb.append(BASE62[n % 62]);
            n = n / 62;
        }
        return sb.reverse().toString();
    }
}
