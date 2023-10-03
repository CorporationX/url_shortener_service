package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    private final int base = 62;
    private final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    public long decode(String number) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Line cannot be null or empty");
        }
        long result = 0;
        int length = number.length();

        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(base, i) * characters.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }

    private String encodeNumber(Long number) {
        StringBuilder builder = new StringBuilder();

        while (number > 0) {
            int reminder = (int) (number % base);
            builder.insert(0, characters.charAt(reminder));
            number /= base;
        }
        return builder.toString();
    }
}