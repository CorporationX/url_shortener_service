package faang.school.urlshortenerservice.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    public List<String> encode(List<Long> numbers) {
        List<String> resultHash = new ArrayList<>(numbers.size());

        for (Long number : numbers) {
            StringBuilder encodedString = new StringBuilder();

            Long currentNumber = number;
            while (currentNumber > 0) {
                int remainder = (int) (currentNumber % BASE);
                encodedString.append(BASE62_ALPHABET.charAt(remainder));
                currentNumber /= BASE;
            }
            if (encodedString.length() > 6){
                log.info("Генерация хеша остановлена для числа " + number + " из-за превышения длины хеша.");
                continue;
            }
            resultHash.add(encodedString.reverse().toString());
        }
        return resultHash;
    }
}