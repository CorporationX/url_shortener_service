package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

@Component
public class Base62 {

    private final static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final static int BASE = ALPHABET.length();

    public String encode(long number) {
        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int mod = (int) number % BASE;
            number = number / BASE;

            result.insert(0, ALPHABET.charAt(mod));
        }

        return result.toString();
    }

    public long decode(String input) {
        long number = 0;

        for (char c : input.toCharArray()) {
            number = number * BASE + ALPHABET.indexOf(c);
        }

        return number;
    }
}
