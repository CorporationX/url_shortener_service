package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.exception.UniqueNumberOutOfBoundsException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CHARACTERS_LENGTH = CHARACTERS.length();

    @Value("${hash.max-length:6}")
    private int maxHashLength;

    private double maxHashNumber;

    @PostConstruct
    public void init() {
        this.maxHashNumber = calculateMaxHashNumber();
    }

    public String encode(long number) {
        if (number > maxHashNumber) {
            throw new UniqueNumberOutOfBoundsException(
                    String.format("Unique number exceeds maximum value for %d-digit: %d", maxHashLength, number)
            );
        }

        if (number == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % CHARACTERS_LENGTH);
            sb.append(CHARACTERS.charAt(remainder));
            number /= CHARACTERS_LENGTH;
        }
        return sb.reverse().toString();
    }

    private long calculateMaxHashNumber() {
        long temp = 1;
        for (int i = 0; i < maxHashLength; i++) {
            temp *= CHARACTERS_LENGTH;
        }
        return temp - 1;
    }
}
