package faang.school.urlshortenerservice.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {

    private final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int BASE_62_SIZE = BASE_62_CHARACTERS.length();

    public List<String> encode(List<Long> numbers) {
        checkNumbers(numbers);

        List<String> hash = new ArrayList<>();

        numbers.forEach((number) -> {
            StringBuilder builder = new StringBuilder();
            while (number > 0) {
                int rem = (int) (number % BASE_62_SIZE);
                builder.append(BASE_62_CHARACTERS.charAt(rem));
                number /= BASE_62_SIZE;
            }
            hash.add(builder.toString());
            log.info("Hash generation is successful");
        });
        return hash;
    }

    private void checkNumbers(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            log.warn("List numbers is null or empty");
            throw new IllegalArgumentException("List must not be null or empty.");
        }
        for (Long numb : numbers) {
            if (numb < 0) {
                log.warn("One of the numbers in the list {} contains negative numbers", numbers);
                throw new IllegalArgumentException("List must contain only Long values.");
            }
        }
    }
}