package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .collect(Collectors.toList());
    }

    private String encode(long num) {
        if (num == 0) {
            return "000000";
        }
        List<Character> result = new ArrayList<>();
        while (num > 0) {
            result.add(BASE62_ALPHABET.charAt((int) (num % BASE)));
            num /= BASE;
        }
        Collections.reverse(result);

        StringBuilder sb = new StringBuilder();
        for (Character ch : result) {
            sb.append(ch);
        }

        String encoded = sb.toString();
        int length = encoded.length();
        if (length < 6) {
            StringBuilder padded = new StringBuilder();
            for (int i = 0; i < 6 - length; i++) {
                padded.append("0");
            }
            padded.append(encoded);
            return padded.toString();
        } else {
            return encoded.substring(0, 6);
        }
    }
}
