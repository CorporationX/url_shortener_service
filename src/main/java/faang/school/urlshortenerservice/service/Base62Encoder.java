package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class Base62Encoder {

    // Алфавит base62
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    // Метод для кодирования списка чисел
    public List<String> encode(List<Long> numbers) {
        List<String> result = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            result.add(encodeNumber(number));
        }
        return result;
    }

    // Перевод одного числа в base62
    private String encodeNumber(Long number) {
        if (number == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int)(number % BASE);
            sb.append(ALPHABET.charAt(remainder));
            number /= BASE;
        }
        return sb.reverse().toString();
    }
}
