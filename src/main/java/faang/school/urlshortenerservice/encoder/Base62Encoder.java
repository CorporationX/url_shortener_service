package faang.school.urlshortenerservice.encoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class Base62Encoder {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_LENGHT = BASE62_ALPHABET.length();
    public List<String> encode(List<Long> numbers) {
        List<String> resultHash = new ArrayList<>(numbers.size());
        for (Long number : numbers) {
            StringBuilder encodedString = new StringBuilder();

            if (number == 0) {
                resultHash.add(String.valueOf(BASE62_ALPHABET.charAt(0)));
                continue;
            }

            Long currentNumber = number;
            while (currentNumber > 0) {
                int remainder = (int) (currentNumber % BASE_LENGHT);
                encodedString.append(BASE62_ALPHABET.charAt(remainder));
                currentNumber /= BASE_LENGHT;
            }
            if (encodedString.length() > 6){
                log.info("Генерация хеша остановлена для числа " + number + " из-за превышения длины хеша.");
                log.info("Hash generation is stopped for " + number + " because of the hash length being exceeded.");
                continue;
            }
            resultHash.add(encodedString.reverse().toString());
        }
        return resultHash;
    }
}