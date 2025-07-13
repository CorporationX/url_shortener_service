package faang.school.urlshortenerservice;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    private static final long MAX_NUMBER = 56800235584L;//62^6
    private static final long SHUFFLE_KEY = 56800235533L;//Prime number, close to MAX_NUMBER

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::shuffleNumber)
                .map(this::encodeToBase62)
                .toList();
    }

    private long shuffleNumber(long number) {
        return (number * SHUFFLE_KEY) % (MAX_NUMBER + 1);
    }

    private String encodeToBase62(long shuffledNumber) {
        StringBuilder sb = new StringBuilder();
        long argumentCopy = shuffledNumber;

        for (int i = 0; i < 6; i++) {
            sb.insert(0, ALPHABET.charAt((int) (argumentCopy % BASE)));
            argumentCopy /= BASE;
        }

        return sb.toString();
    }
}
