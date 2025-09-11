package faang.school.urlshortenerservice.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder implements Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_62 = ALPHABET.length();

    @Override
    public List<String> encode(List<Long> numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("numbers must not be null");
        }
        List<String> encodedNumbers = new ArrayList<>(numbers.size());
        numbers.forEach(number -> {
            if (number == null || number <= 0) {
                throw new IllegalArgumentException("Sequence value must be positive (> 0)");
            }
            encodedNumbers.add(toBase62(number));
        });
        return encodedNumbers;
    }

    private String toBase62(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE_62);
            sb.append(ALPHABET.charAt(remainder));
            number /= BASE_62;
        }
        return sb.reverse().toString();
    }
}
