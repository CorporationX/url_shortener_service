package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String CHARSET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public String encode(long num) {
        if (num == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(CHARSET.charAt((int) (num % BASE)));
            num /= BASE;
        }
        return sb.reverse().toString();
    }

    public long decode(String str) {
        long result = 0;
        for (char c : str.toCharArray()) {
            result = result * BASE + CHARSET.indexOf(c);
        }
        return result;
    }
}
