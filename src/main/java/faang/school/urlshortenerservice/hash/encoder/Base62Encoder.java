package faang.school.urlshortenerservice.hash.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder implements Base62 {
    @Value("${encoder.base62.base}")
    private int base;
    @Value("${encoder.base62.characters}")
    private String characters;

    @Override
    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder(1);
        do {
            stringBuilder.insert(0, characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }

    @Override
    public long decode(String number) {
        long result = 0L;
        int length = number.length();
        for (int i = 0; i < length; i++) {
            result += (long) Math.pow(base, i) * characters.indexOf(number.charAt(length - i - 1));
        }
        return result;
    }

    @Override
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .collect(Collectors.toList());
    }
}
