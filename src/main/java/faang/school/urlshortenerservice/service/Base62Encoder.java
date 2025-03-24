package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int BASE_62_SIZE = BASE_62_CHARACTERS.length();

    public List<String> generateHashes(List<Long> numbers) {
        validateNumbers(numbers);

        return numbers.stream()
                .map(number -> {
                    StringBuilder builder = new StringBuilder();
                    while (number > 0) {
                        int rem = (int) (number % BASE_62_SIZE);
                        builder.append(BASE_62_CHARACTERS.charAt(rem));
                        number /= BASE_62_SIZE;
                    }
                    return builder.toString();
                })
                .toList();
    }

    private void validateNumbers(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("List must not be empty");
        }
        for (Long numb : numbers) {
            if (numb < 0) {
                throw new IllegalArgumentException("List must contain positive values");
            }
        }
    }
}
