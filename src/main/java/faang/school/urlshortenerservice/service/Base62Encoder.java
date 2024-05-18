package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {
    private final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int BASE = 62;

    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>();
        for (Long number : numbers) {
            result.add(encode(number));
        }
        return result;
    }

    public List<Long> decode(List<String> numbers) {
        List<Long> result = new ArrayList<>();
        for (String number : numbers) {
            result.add(decode(number));
        }
        return result;
    }

    private String encode(Long number) {
        StringBuilder result = new StringBuilder(1);
        do {
            result.insert(0, BASE62.charAt((int) (number % BASE)));
            number /= BASE;
        } while (number > 0);
        return result.toString();
    }

    private Long decode(String number) {
        long result = 0;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(BASE, i) * BASE62.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }
}
