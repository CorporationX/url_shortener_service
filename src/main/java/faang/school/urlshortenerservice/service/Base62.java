package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

@Component
public class Base62 {

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int BASE = CHARS.length();

    public String encode(long input) {
        StringBuilder encoded = new StringBuilder();
        if (input == 0) {
            return "0";
        }
        while (input > 0) {
            int remainder = (int) (input % BASE);
            encoded.insert(0, CHARS.charAt(remainder));
            input /= BASE;
        }
        return encoded.toString();
    }

    public long decode(String encoded) {
        long decoded = 0;
        for (int i = 0; i < encoded.length(); i++) {
            int index = CHARS.indexOf(encoded.charAt(i));
            decoded = decoded * BASE + index;
        }
        return decoded;
    }
}
