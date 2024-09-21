package faang.school.urlshortenerservice.generate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        List<String> encodedNumbers = new ArrayList<>();
        for (Long number : numbers) {
            StringBuilder encoded = new StringBuilder();
            while (number > 0) {
                int remainder = (int) (number % BASE);
                encoded.append(ALPHABET.charAt(remainder));
                number /= BASE;
            }
            encodedNumbers.add(encoded.reverse().toString());
        }
        return encodedNumbers;
    }
}
